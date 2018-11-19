package de.pavloff.pycharm.plugin.varviewer;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import de.pavloff.pycharm.BaseUtils;
import de.pavloff.pycharm.plugin.ipnb.ConnectionManager;
import de.pavloff.pycharm.plugin.recomcode.RecomCodeManager;
import de.pavloff.pycharm.plugin.serverstub.ServerStub;
import de.pavloff.pycharm.plugin.serverstub.ServerStubFactory;
import org.jetbrains.plugins.ipnb.editor.panels.code.IpnbErrorPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FilterInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/** Plugin code which displays pandas dataframe as a table
 */
class DataframeTab extends JPanel {

    // the dataframe is created once the tab is open
    private Boolean isOpened = false;

    private JBTable tableView;

    private static Logger logger = Logger.getInstance(DataframeTab.class);

    DataframeTab() {
        setLayout(new BorderLayout());
    }

    /**
     * displays the content of selected tab
     * reads the dataframe if opened first time
     */
    void open(Project openedProject, VirtualFile openedFile, String name) {
        String varName = name.split(" ")[1];
        logger.debug(String.format("opening tab '%s'..", varName));

        if (tableView != null) {
            ServerStub serverStub = ServerStubFactory.getInstance();
            serverStub.onDataframe(varName, tableView.getModel());
            RecomCodeManager recomCodeManager = RecomCodeManager.getInstance(openedProject);
            recomCodeManager.updateAndDisplayRecommendations();
        }
        if (isOpened) {
            return;
        }

        String toCSV = "";
        URL resources = BaseUtils.getResource("/python/df_to_csv.py");
        try {
            FilterInputStream in = (FilterInputStream) resources.getContent();
            int bytesRead;
            byte[] bytes = new byte[128];
            while ((bytesRead = in.read(bytes)) != -1) {
                toCSV = String.format(new String(bytes, 0, bytesRead),
                        varName, BaseUtils.DELIMITER, BaseUtils.LINE_SEP_ESC);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("reading dataframe..");
        ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);
        ipnb.execute(openedFile, toCSV, new ConnectionManager.Output() {
            @Override
            public void onFinished(List<String> output, List<String> error, List<String> payload) {
                if (payload != null) {
                    logger.debug("dataframe read. payload as result. ignoring..");
                }

                if (error != null) {
                    logger.debug("code executed. error as result. showing error traceback..");
                    show(IpnbErrorPanel.createColoredPanel(error));

                } else {
                    logger.debug("dataframe read. output evaluating..");

                    if (output == null || output.size() == 0) {
                        logger.debug("..output is null");

                    } else {
                        String combinedOutput;
                        if (output.size() != 1) {
                            combinedOutput = String.join(BaseUtils.LINE_SEP, output);
                        } else {
                            combinedOutput = output.get(0);
                        }

                        String[] outputLines = combinedOutput.split(BaseUtils.LINE_SEP);

                        String[] header = outputLines[0].split(BaseUtils.DELIMITER);

                        if (BaseUtils.hasHeader(outputLines) < 0) {
                            logger.debug("..dataframe with header");
                            header = new String[header.length];
                            for (int i = 0; i < header.length; i++) {
                                header[i] = "column" + i;
                            }
                        } else {
                            logger.debug("..dataframe without header");
                            outputLines = Arrays.copyOfRange(outputLines, 1, outputLines.length);
                        }

                        int linesToShow = Math.min(outputLines.length, 1000);
                        logger.debug(String.format("..show %s lines", linesToShow));

                        String[][] data = new String[linesToShow][header.length];
                        for (int i = 0; i < linesToShow; i++) {
                            data[i] = outputLines[i].split(BaseUtils.DELIMITER);
                        }

                        logger.debug("creating table view..");
                        tableView = new JBTable(new NotEditableTableModel(data, header));
                        JTableHeader tableHeader = tableView.getTableHeader();
                        tableHeader.setFont(new Font("Default", Font.BOLD, 16));

                        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        tableView.setCellSelectionEnabled(true);
                        tableView.setColumnSelectionAllowed(true);

                        ListSelectionModel columnSelectionModel = tableView.getColumnModel().getSelectionModel();
                        columnSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                        columnSelectionModel.addListSelectionListener(new SelectionListener(openedProject,
                                tableView));

                        ListSelectionModel rowSelectionModel = tableView.getSelectionModel();
                        rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                        rowSelectionModel.addListSelectionListener(new SelectionListener(openedProject,
                                tableView));

                        show(tableView);
                        logger.debug("table view created");

                        ServerStub serverStub = ServerStubFactory.getInstance();
                        serverStub.onDataframe(varName, tableView.getModel());
                        RecomCodeManager recomCodeManager = RecomCodeManager.getInstance(openedProject);
                        recomCodeManager.updateAndDisplayRecommendations();
                    }
                }
            }
        });

        isOpened = true;
    }

    /**
     * puts tab content in a scroll pane
     */
    private void show(JComponent panel) {
        removeAll();
        add(new JBScrollPane(panel));
        revalidate();
        repaint();
    }

    /**
     * catches the changes on table,
     * sends the events to ServerStub and
     * triggers the update of recommendations in RecomCode panel
     */
    private static class SelectionListener implements ListSelectionListener {

        private Project project;
        private JBTable table;

        SelectionListener(Project openedProject, JBTable tableView) {
            project = openedProject;
            table = tableView;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                return;
            }

            List<Pair<Integer, Integer>> cells = new LinkedList<>();

            int rowIndexEnd = table.getSelectionModel().getMaxSelectionIndex();
            int colIndexEnd = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();

            for (int i = table.getSelectedRow(); i <= rowIndexEnd; i++) {
                for (int j = table.getSelectedColumn(); j <= colIndexEnd; j++) {
                    if (table.isCellSelected(i, j)) {
                        Pair<Integer, Integer> cell = new Pair<>(i, j);
                        cells.add(cell);
                    }
                }
            }

            ServerStub serverStub = ServerStubFactory.getInstance();
            if (cells.size() == 1) {
                serverStub.onCell(cells.get(0).first, cells.get(0).second);
            } else {
                serverStub.onCells(cells);
            }

            RecomCodeManager recomCodeManager = RecomCodeManager.getInstance(project);
            recomCodeManager.updateAndDisplayRecommendations();
        }
    }

    /**
     * extends the default table model with not editable cells
     */
    private class NotEditableTableModel extends DefaultTableModel {
        NotEditableTableModel(String[][] data, String[] header) {
            super(data, header);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
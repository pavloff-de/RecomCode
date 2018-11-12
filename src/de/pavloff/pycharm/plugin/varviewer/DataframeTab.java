package de.pavloff.pycharm.plugin.varviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import de.pavloff.pycharm.BaseUtils;
import de.pavloff.pycharm.plugin.ipnb.ConnectionManager;
import de.pavloff.pycharm.plugin.ipnb.OutputCell;
import de.pavloff.pycharm.plugin.recomcode.RecomCodeManager;
import de.pavloff.pycharm.plugin.server_stub.ServerStub;
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

class DataframeTab extends JPanel {

    private Boolean isOpened = false;
    private JBTable tableView;

    DataframeTab() {
        setLayout(new BorderLayout());
    }

    void open(Project openedProject, VirtualFile openedFile, String name) {
        String varName = name.split(" ")[1];

        if (tableView != null) {
            ServerStub serverStub = ServerStub.getInstance(openedProject);
            serverStub.onDataframe(varName, tableView.getModel());
            RecomCodeManager recomCodeManager = RecomCodeManager.getInstance(openedProject);
            recomCodeManager.updateAndDisplayRecommendations();
        }
        if (isOpened) {
            return;
        }

        String toCSV = "";
        URL resources = DataframeTab.class.getResource("python/df_to_csv.py");
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

        ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);

        ipnb.execute(openedFile, toCSV, new OutputCell() {
            @Override
            public void onOutput(List<String> fromIpnb) {
                isOpened = true;

                String output;
                if (fromIpnb.size() != 1) {
                    output = String.join(BaseUtils.LINE_SEP, fromIpnb);
                } else {
                    output = fromIpnb.get(0);
                }

                String[] outputLines = output.split(BaseUtils.LINE_SEP);
                if (outputLines.length == 0) {
                    return;
                }

                String[] header = outputLines[0].split(BaseUtils.DELIMITER);

                if (BaseUtils.hasHeader(outputLines) < 0) {
                    header = new String[header.length];
                    for (int i = 0; i < header.length; i++) {
                        header[i] = "column" + i;
                    }
                } else {
                    outputLines = Arrays.copyOfRange(outputLines, 1, outputLines.length);
                }

                int linesToShow = Math.min(outputLines.length, 1000);

                String[][] data = new String[linesToShow][header.length];
                for (int i = 0; i < linesToShow; i++) {
                    data[i] = outputLines[i].split(BaseUtils.DELIMITER);
                }

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

                ServerStub serverStub = ServerStub.getInstance(openedProject);
                serverStub.onDataframe(varName, tableView.getModel());
                RecomCodeManager recomCodeManager = RecomCodeManager.getInstance(openedProject);
                recomCodeManager.updateAndDisplayRecommendations();
            }

            @Override
            public void onPayload(String payload) {
            }

            @Override
            public void onError(String eName, String eValue, List<String> traceback) {
                show(IpnbErrorPanel.createColoredPanel(traceback));
            }
        });
    }

    private static class SelectionListener implements ListSelectionListener {
        private ServerStub serverStub;
        private Project project;
        private JBTable table;

        SelectionListener(Project openedProject, JBTable tableView) {
            project = openedProject;
            table = tableView;
            serverStub = ServerStub.getInstance(project);
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

            if (cells.size() == 1) {
                serverStub.onCell(cells.get(0).first, cells.get(0).second);
            } else {
                serverStub.onCells(cells);
            }
            RecomCodeManager recomCodeManager = RecomCodeManager.getInstance(project);
            recomCodeManager.updateAndDisplayRecommendations();
        }
    }

    private void show(JComponent panel) {
        removeAll();
        add(new JBScrollPane(panel));
        revalidate();
        repaint();
    }

    private class NotEditableTableModel extends DefaultTableModel {
        public NotEditableTableModel(String[][] data, String[] header) {
            super(data, header);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
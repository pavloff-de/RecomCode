package de.pavloff.pycharm.plugin.varviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import de.pavloff.pycharm.BaseUtils;
import de.pavloff.pycharm.core.CodeFragmentManager;
import de.pavloff.pycharm.ipnb.ConnectionManager;
import de.pavloff.pycharm.ipnb.OutputCell;
import de.pavloff.pycharm.plugin.BaseConstants;
import org.jetbrains.plugins.ipnb.editor.panels.code.IpnbErrorPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class DataframeTab extends JPanel implements BaseConstants {

    private Boolean isOpened = false;

    DataframeTab() {
        setLayout(new BorderLayout());
    }

    void open(Project openedProject, VirtualFile openedFile, String name) {
        if (isOpened) {
            return;
        }

        String toCSV = "";
        URL resources = DataframeTab.class.getResource("python/df_to_csv.py");
        try {
            BufferedInputStream in = (BufferedInputStream) resources.getContent();
            int bytesRead;
            byte[] bytes = new byte[128];
            while ((bytesRead = in.read(bytes)) != -1) {
                toCSV = String.format(new String(bytes, 0, bytesRead),
                        name.split(" ")[1], DELIMITER, LINE_SEP_ESC);
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
                    output = String.join(LINE_SEP, fromIpnb);
                } else {
                    output = fromIpnb.get(0);
                }

                String[] outputLines = output.split(LINE_SEP);
                if (outputLines.length == 0) {
                    return;
                }

                String[] header = outputLines[0].split(DELIMITER);

                if (BaseUtils.hasHeader(outputLines) < 0) {
                    header = new String[header.length];
                    for (int i = 0; i < header.length; i++) {
                        header[i] = "column" + i;
                    }
                } else {
                    outputLines = Arrays.copyOfRange(outputLines, 1, outputLines.length);
                }

                String[][] data = new String[outputLines.length][header.length];
                for (int i = 0; i < outputLines.length; i++) {
                    data[i] = outputLines[i].split(DELIMITER);
                }
                JBTable tableView = new JBTable(new DefaultTableModel(data, header));
                tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tableView.setCellSelectionEnabled(true);
                tableView.getSelectionModel().addListSelectionListener(new SelectionListener(openedProject, tableView));
                show(tableView);
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

            CodeFragmentManager manager = CodeFragmentManager.getInstance(project);
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
                manager.cellSelected(cells.get(0).first, cells.get(0).second);
            } else {
                manager.cellsSelected(cells);
            }
        }
    }

    private void show(JComponent panel) {
        removeAll();
        add(new JBScrollPane(panel));
        revalidate();
    }
}
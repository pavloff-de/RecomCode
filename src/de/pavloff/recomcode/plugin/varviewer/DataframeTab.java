package de.pavloff.recomcode.plugin.varviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import de.pavloff.recomcode.ipnb.ConnectionManager;
import de.pavloff.recomcode.ipnb.OutputCell;
import de.pavloff.recomcode.plugin.BaseConstants;
import org.jetbrains.plugins.ipnb.editor.panels.code.IpnbErrorPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class DataframeTab extends JPanel implements BaseConstants {

    private Boolean isOpened = false;

    public DataframeTab() {
        setLayout(new BorderLayout());
    }

    public void open(Project openedProject, VirtualFile openedFile, String name) {
        if (isOpened) {
            return;
        }

        String toCSV = "";
        URL resources = DataframeTab.class.getResource("python/df_to_csv.py");
        try {
            BufferedInputStream in = (BufferedInputStream) resources.getContent();
            int bytesRead;
            byte[] bytes = new byte[128];
            while((bytesRead = in.read(bytes)) != -1) {
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

                if (hasHeader(outputLines) < 0) {
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

    private void show(JComponent panel) {
        removeAll();
        add(new JBScrollPane(panel));
        revalidate();
    }

    private Class guessValueType(String value) {
        try {
            Integer.valueOf(value);
            return Integer.class;
        } catch (NumberFormatException ignored) {
        }

        try {
            Float.valueOf(value);
            return Float.class;
        } catch (NumberFormatException ignored) {
        }

        if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
            return Boolean.class;
        }

        return null;
    }

    private int hasHeader(String[] lines) {
        // -1 if not found
        // 0 if names in the first line
        if (lines.length == 1) {
            return 0;
        }

        String[] vals1 = lines[0].split(DELIMITER);
        String[] vals2 = lines[1].split(DELIMITER);

        if (vals1.length != vals2.length) {
            return -1;
        }

        int numOfMatches = 0;
        for (int i = 0; i < vals1.length; i++) {
            if (guessValueType(vals1[i]) == guessValueType(vals2[i])) {
                numOfMatches += 1;
            }
        }

        if (numOfMatches != vals1.length) {
            // different values in first two lines
            // points at a header in first line
            return 0;
        }

        return -1;
    }
}
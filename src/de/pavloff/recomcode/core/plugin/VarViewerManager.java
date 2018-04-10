package de.pavloff.recomcode.core.plugin;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditorLocation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import de.pavloff.recomcode.core.ipnb.ConnectionManager;
import de.pavloff.recomcode.core.ipnb.OutputCell;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class VarViewerManager {

    private JPanel varViewer;
    private JBTabbedPane tabbedPane;
    private Project openedProject;

    private String VAR_VIEWER_SEP = "### var viewer output ###";
    private String LINE_SEP = "\n";
    private String LINE_SEP_ESC = "\\n";
    private String DELIMITER = ",";
    private String OUTPUT_TAB = "Output";

    public static VarViewerManager getInstance(Project project) {
        return project.getComponent(VarViewerManager.class);
    }

    public JComponent initView(Project project) {
        openedProject = project;

        varViewer = new JPanel();
        varViewer.setLayout(new BoxLayout(varViewer, BoxLayout.Y_AXIS));
        tabbedPane = new JBTabbedPane();
        tabbedPane.addChangeListener(e -> onTabOpen());

        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("VarViewer.Toolbar");
        ActionToolbar actionToolbar = actionManager.createActionToolbar("VarViewer.Toolbar.ID", actionGroup, true);
        Component t = actionToolbar.getComponent();
        t.setMaximumSize(new Dimension(200, 20));

        varViewer.add(t);
        varViewer.add(tabbedPane);

        initConn();

        return varViewer;
    }

    public void executeCode() {
        VirtualFile openedFile = getOpenedFile();

        if (openedFile == null) {
            return;
        }

        int cursorPosition = getCursorPosition() + 1;
        if (cursorPosition == 0) {
            return;
        }

        StringBuilder codeContent = new StringBuilder();
        try {
            LineNumberReader reader = new LineNumberReader(
                    new BufferedReader(new InputStreamReader(openedFile.getInputStream())));

            while (reader.getLineNumber() < cursorPosition) {
                String line = reader.readLine();
                codeContent.append(line).append(LINE_SEP);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        codeContent.append(String.format("print '%s'", VAR_VIEWER_SEP)).append(LINE_SEP);
        URL resources = VarViewerManager.class.getResource("python/var_viewer.py");
        try {
            StringBuilder content = new StringBuilder();
            BufferedInputStream in = (BufferedInputStream) resources.getContent();
            int bytesRead;
            byte[] bytes = new byte[256];
            while((bytesRead = in.read(bytes)) != -1) {
                content.append(new String(bytes, 0, bytesRead));
            }

            codeContent.append(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String code = codeContent.toString();
        ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);
        ipnb.execute(openedFile, code, new OutputCell() {
            @Override
            public void onOutput(List<String> output) {
                createTabs(output);
            }

            @Override
            public void onPayload(String payload) {
                // ???
                createTabs(Collections.singletonList(payload));
            }
        });
    }

    private void initConn() {
        VirtualFile openedFile = getOpenedFile();
        if (openedFile == null) {
            return;
        }
        ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);
        ipnb.initConnection(openedFile);
    }

    private FileEditor getOpenedEditor() {
        FileEditor[] editors = FileEditorManager.getInstance(openedProject).getSelectedEditors();

        if (editors.length == 1) {
            return editors[0];
        }

        return null;
    }

    private VirtualFile getOpenedFile() {
        FileEditor editor = getOpenedEditor();

        if (editor == null) {
            return null;
        }

        return editor.getFile();
    }

    private int getCursorPosition() {
        FileEditor editor = getOpenedEditor();
        if (editor == null) {
            return -1;
        }

        TextEditorLocation location = (TextEditorLocation) editor.getCurrentLocation();
        if (location == null) {
            return -1;
        }

        return location.getPosition().line;
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

        try {
            Boolean.valueOf(value);
            return Boolean.class;
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    private int hasHeader(String[] lines) {
        // -1 if not found
        // 0 if names in the first line
        if (lines.length == 1) {
            return 0;
        }

        String[] vals1 = lines[0].split(LINE_SEP);
        String[] vals2 = lines[1].split(LINE_SEP);

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

    private void onTabOpen() {
        int tabIdx = tabbedPane.getSelectedIndex();
        if (tabIdx < 0) {
            return;
        }

        String tabName = tabbedPane.getTitleAt(tabIdx);
        if (tabName.equals(OUTPUT_TAB)) {
            return;
        }

        String dfName = tabName.split(" ")[1];
        String code = String.format("print %s.to_csv(index=False, sep='%s', header=True, line_terminator='%s')", dfName, DELIMITER, LINE_SEP_ESC);

        ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);
        ipnb.execute(getOpenedFile(), code, new OutputCell() {
            @Override
            public void onOutput(List<String> fromIpnb) {
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
                }

                String[][] data = new String[outputLines.length][header.length];
                for (int i = 0; i < outputLines.length; i++) {
                    data[i] = outputLines[i].split(DELIMITER);
                }

                try {
                    JBTable table = (JBTable) tabbedPane.getComponentAt(tabIdx);
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    tableModel.setDataVector(data, header);
                    tabbedPane.revalidate();
                } catch (ClassCastException ignored) {
                    // run again
                    tabbedPane.setSelectedIndex(tabIdx);
                }

            }

            @Override
            public void onPayload(String payload) {
                // ???
                createTabs(Collections.singletonList(payload));
            }
        });
    }

    private void onDataFrame(String dfName) {
        JComponent dfTab = new JBTable(new DefaultTableModel());
        tabbedPane.addTab(dfName, dfTab);
    }

    private void onOutput(String output) {
        JComponent outputPanel = new JPanel(false);
        outputPanel.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(output);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JBScrollPane(textArea);
        outputPanel.add(scrollPane);

        tabbedPane.insertTab(OUTPUT_TAB, null, outputPanel, null, 0);
        tabbedPane.setSelectedIndex(0);
    }

    private void createTabs(List<String> fromIpnb) {
        String output;
        if (fromIpnb.size() != 1) {
            output = String.join(LINE_SEP, fromIpnb);
        } else {
            output = fromIpnb.get(0);
        }

        tabbedPane.removeAll();

        String[] outputLines = output.split(LINE_SEP);
        StringBuilder mainOutput = new StringBuilder();
        boolean varViewerOutputFound = false;

        for (String s : outputLines) {
            if (s.startsWith(VAR_VIEWER_SEP)) {
                varViewerOutputFound = true;

            } else if (varViewerOutputFound) {
                if (s.startsWith("DataFrame ")) {
                    onDataFrame(s);
                }

            } else {
                mainOutput.append(s).append(LINE_SEP);
            }
        }
        onOutput(mainOutput.toString());

        tabbedPane.revalidate();
        varViewer.revalidate();
    }
}

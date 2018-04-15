package de.pavloff.recomcode.core.plugin.varviewer;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import de.pavloff.recomcode.core.ipnb.ConnectionManager;
import de.pavloff.recomcode.core.ipnb.OutputCell;
import org.jetbrains.plugins.ipnb.editor.panels.code.IpnbErrorPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
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

    JComponent initView(Project project) {
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

        int cursorPosition = getCursorPosition();
        if (cursorPosition == 0) {
            return;
        }

        StringBuilder codeContent = new StringBuilder();

        FileDocumentManager fileManager = FileDocumentManager.getInstance();
        Document doc = fileManager.getDocument(openedFile);
        codeContent.append(doc.getText(new TextRange(0, doc.getLineEndOffset(cursorPosition))));
        codeContent.append(LINE_SEP);
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

            @Override
            public void onError(String eName, String eValue, List<String> traceback) {
                tabbedPane.removeAll();
                createErrorOutputTab(IpnbErrorPanel.createColoredPanel(traceback));
            }
        });
    }

    private void initConn() {
        VirtualFile openedFile = getOpenedFile();

        // FileEditorManagerListener
        // fileClosed
        // selectionChanged

        // DocumentListener
        // documentChanged

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
                } else {
                    outputLines = Arrays.copyOfRange(outputLines, 1, outputLines.length);
                }

                String[][] data = new String[outputLines.length][header.length];
                for (int i = 0; i < outputLines.length; i++) {
                    data[i] = outputLines[i].split(DELIMITER);
                }

                try {
                    DefaultTableModel tableModel = new DefaultTableModel(data, header);
                    tabbedPane.setComponentAt(tabIdx, createTabPanel(new JBTable(tableModel)));
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

            @Override
            public void onError(String eName, String eValue, List<String> traceback) {
                tabbedPane.remove(tabbedPane.getComponentAt(tabIdx));
                tabbedPane.setComponentAt(tabIdx, IpnbErrorPanel.createColoredPanel(traceback));
                tabbedPane.revalidate();

            }
        });
    }

    private JComponent createTabPanel(String output) {
        JTextArea textArea = new JTextArea(output);
        textArea.setEditable(false);
        return createTabPanel(textArea);
    }

    private JComponent createTabPanel(JComponent panel) {
        panel.setLayout(new BorderLayout());
        return new JBScrollPane(panel);
    }

    private void createErrorOutputTab(JComponent tracebackPanel) {
        tabbedPane.insertTab(OUTPUT_TAB, null, createTabPanel(tracebackPanel), null, 0);
    }

    private void createOutputTab(String output) {
        tabbedPane.insertTab(OUTPUT_TAB, null, createTabPanel(output), null, 0);
    }

    private void createDataframeTab(String dfName) {
        tabbedPane.addTab(dfName, new JPanel());
    }

    private void createTabs(List<String> fromIpnb) {
        String output;
        if (fromIpnb.size() != 1) {
            output = String.join(LINE_SEP, fromIpnb);
        } else {
            output = fromIpnb.get(0);
        }

        String[] outputLines = output.split(LINE_SEP);
        StringBuilder mainOutput = new StringBuilder();
        LinkedList<String> dfOutput = new LinkedList<>();
        boolean varViewerOutputFound = false;

        for (String s : outputLines) {
            if (s.startsWith(VAR_VIEWER_SEP)) {
                varViewerOutputFound = true;

            } else if (varViewerOutputFound) {
                if (s.startsWith("DataFrame ")) {
                    dfOutput.add(s);
                }
            } else {
                mainOutput.append(s).append(LINE_SEP);
            }
        }

        int openedTab = tabbedPane.getSelectedIndex();
        if (openedTab < 0) {
            openedTab = 0;
        }

        tabbedPane.removeAll();

        createOutputTab(mainOutput.toString());
        for (String s : dfOutput) {
            createDataframeTab(s);
        }

        try {
            tabbedPane.setSelectedIndex(openedTab);
        } catch (IndexOutOfBoundsException ignored) {
            tabbedPane.setSelectedIndex(0);
        }

        tabbedPane.revalidate();
        varViewer.revalidate();
    }
}

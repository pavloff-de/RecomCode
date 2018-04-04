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
import de.pavloff.recomcode.core.ipnb.ConnectionManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.List;

public class VarViewerManager {

    private JPanel varViewer;
    private JBTabbedPane tabbedPane;
    private Project openedProject;
    private ConnectionManager ipnb;

    private String VARVIEWER_SEP = "### var viewer output ###";
    private String LINE_SEP = "\n";

    public VarViewerManager(Project project) {
        openedProject = project;
        ipnb = new ConnectionManager(project);
        initPane();
        initConn();
    }

    public static VarViewerManager getInstance(Project project) {
        return project.getComponent(VarViewerManager.class);
    }

    private void initPane() {
        varViewer = new JPanel();
        varViewer.setLayout(new BoxLayout(varViewer, BoxLayout.Y_AXIS));
        tabbedPane = new JBTabbedPane();

        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("VarViewer.Toolbar");
        ActionToolbar actionToolbar = actionManager.createActionToolbar("", actionGroup, true);
        Component t = actionToolbar.getComponent();
        t.setMaximumSize(new Dimension(200, 20));

        varViewer.add(t);
        varViewer.add(tabbedPane);
    }

    public JComponent getView() {
        return varViewer;
    }

    public void initConn(VirtualFile openedFile) {
        if (openedFile == null) {
            return;
        }
        ipnb.initConnection(openedFile);
    }

    public void initConn() {
        initConn(getOpenedFile());
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

        codeContent.append(String.format("print '%s'", VARVIEWER_SEP)).append(LINE_SEP);
        URL resources = VarViewerManager.class.getResource("python/var_viewer.py");
        try {
            String content = "";
            BufferedInputStream in = (BufferedInputStream) resources.getContent();
            int bytesRead = 0;
            byte[] bytes = new byte[256];
            while((bytesRead = in.read(bytes)) != -1) {
                content += new String(bytes, 0, bytesRead);
            }

            codeContent.append(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String code = codeContent.toString();
        ipnb.execute(openedFile, code);
    }

    private void onDataFrame(String dfName) {
        JComponent dfTab = new JPanel(false);
        tabbedPane.addTab(dfName, dfTab);
    }

    private void onOutput(String output) {
        JComponent outputPanel = new JPanel(false);
        JTextArea textArea = new JTextArea(output);
        JScrollPane scrollPane = new JBScrollPane(textArea);
        textArea.setEditable(false);
        outputPanel.add(scrollPane);
        tabbedPane.addTab("Output", outputPanel);
    }

    public void setVars(List<String> text) {
        tabbedPane.removeAll();

        if (text.size() != 1) {
            return;
        }

        String[] outputLines = text.get(0).split(LINE_SEP);
        StringBuilder mainOutput = new StringBuilder();
        boolean varViewerOutputReached = false;

        for (String s : outputLines) {
            if (s.startsWith(VARVIEWER_SEP)) {
                onOutput(mainOutput.toString());
                varViewerOutputReached = true;

            } else if (varViewerOutputReached) {
                if (s.startsWith("DataFrame ")) {
                    onDataFrame(s);
                }

            } else {
                mainOutput.append(s).append(LINE_SEP);
            }
        }

        tabbedPane.revalidate();
        varViewer.revalidate();
    }
}

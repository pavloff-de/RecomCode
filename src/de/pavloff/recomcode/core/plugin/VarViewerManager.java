package de.pavloff.recomcode.core.plugin;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import de.pavloff.recomcode.core.ipnb.Ipnb;

import javax.swing.*;

public class VarViewerManager {

    private JSplitPane mainPane;
    private JPanel varViewer;
    private JTextArea logPane;
    private Ipnb ipnb;

    public VarViewerManager(Project project) {
        ipnb = new Ipnb(project);
        initPane();
        initConn(project);
    }

    private void log(String text) {
        logPane.append(text);
    }

    private void initPane() {
        varViewer = new JPanel();
        logPane = new JTextArea();
        logPane.setFocusable(false);

        mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, varViewer, new JBScrollPane(logPane));
        mainPane.setOneTouchExpandable(true);
        mainPane.setResizeWeight(0.8);
    }

    public JComponent getView() {
        return mainPane;
    }

    public void initConn(Project project, VirtualFile openedFile) {
        if (openedFile == null) {
            return;
        }
        log("Init Connection ..");
        ipnb.initConnection(project, openedFile.getPath());
    }

    public void initConn(Project project) {
        FileEditor[] editors = FileEditorManager.getInstance(project).getSelectedEditors();

        for (FileEditor e : editors) {
            initConn(project, e.getFile());
        }
    }
}

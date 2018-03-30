package de.pavloff.recomcode.core.plugin;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.pavloff.recomcode.core.ipnb.ConnectionManager;

import javax.swing.*;
import java.util.List;

public class VarViewerManager {

    private JPanel varViewer;
    private Project openedProject;
    private ConnectionManager ipnb;

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

        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("VarViewer.Toolbar");
        ActionToolbar actionToolbar = actionManager.createActionToolbar("", actionGroup, true);
        varViewer.add(actionToolbar.getComponent());
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

    private VirtualFile getOpenedFile() {
        VirtualFile openedFile = null;
        FileEditor[] editors = FileEditorManager.getInstance(openedProject).getSelectedEditors();
        for (FileEditor e : editors) {
            openedFile = e.getFile();
        }
        return openedFile;
    }

    public void getVarsFromCode(String code) {
        VirtualFile openedFile = getOpenedFile();
        if (openedFile != null) {
            List<String> out = ipnb.execute(openedFile, code);
            for (String o : out) {
                varViewer.add(new JLabel(o));
            }
            varViewer.revalidate();
            varViewer.repaint();
        }
    }
}

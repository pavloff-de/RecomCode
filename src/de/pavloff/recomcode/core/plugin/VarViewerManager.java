package de.pavloff.recomcode.core.plugin;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditorLocation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.pavloff.recomcode.core.ipnb.ConnectionManager;

import javax.swing.*;
import java.io.*;

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

    public void getVarsFromCode() {
        VirtualFile openedFile = getOpenedFile();

        if (openedFile == null) {
            return;
        }

        int cursorPosition = getCursorPosition() + 1;
        if (cursorPosition == 0) {
            return;
        }

        String code = "";
        try {
            LineNumberReader reader = new LineNumberReader(
                    new BufferedReader(new InputStreamReader(openedFile.getInputStream())));
            StringBuilder codeContent = new StringBuilder();

            while (reader.getLineNumber() < cursorPosition) {
                String line = reader.readLine();
                codeContent.append(line).append(openedFile.getDetectedLineSeparator());
            }
            code = codeContent.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ipnb.execute(openedFile, code);
        //TODO: variable inspector ?
    }
}

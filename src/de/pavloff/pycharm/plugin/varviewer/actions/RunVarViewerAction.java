package de.pavloff.pycharm.plugin.varviewer.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import de.pavloff.pycharm.plugin.varviewer.VarViewerManager;

public class RunVarViewerAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VarViewerManager manager = VarViewerManager.getInstance(project);
        manager.executeCode();
    }
}

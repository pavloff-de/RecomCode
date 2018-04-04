package de.pavloff.recomcode.core.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import de.pavloff.recomcode.core.plugin.VarViewerManager;

public class RunCodeAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VarViewerManager manager = VarViewerManager.getInstance(project);
        manager.executeCode();
    }
}

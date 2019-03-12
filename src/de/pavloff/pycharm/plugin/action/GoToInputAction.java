package de.pavloff.pycharm.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import de.pavloff.pycharm.plugin.recomcode.RecomCodeManager;
import org.jetbrains.annotations.NotNull;

public class GoToInputAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project openedProject = e.getProject();
        if (openedProject == null) {
            return;
        }

        ToolWindowManager.getInstance(openedProject).getToolWindow("RecomCode").show(() -> RecomCodeManager.getInstance(openedProject).focusIn());
    }
}

package de.pavloff.pycharm.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.pavloff.pycharm.plugin.varviewer.VarViewerManager;
import org.jetbrains.annotations.NotNull;

public class RunAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VarViewerManager.getInstance(e.getProject()).executeCode();
    }
}

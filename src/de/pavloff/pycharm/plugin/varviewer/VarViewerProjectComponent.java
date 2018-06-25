package de.pavloff.pycharm.plugin.varviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class VarViewerProjectComponent implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        VarViewerManager manager = VarViewerManager.getInstance(project);
        toolWindow.getComponent().add(manager.initView(project));
    }
}
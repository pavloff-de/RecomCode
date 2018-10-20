package de.pavloff.pycharm.plugin.varviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import de.pavloff.pycharm.plugin.server_stub.ServerStub;
import org.jetbrains.annotations.NotNull;

public class VarViewerProjectComponent implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        VarViewerManager manager = VarViewerManager.getInstance(project);
        toolWindow.getComponent().add(manager.initView(project));

        // ServerStub replaces previous usage of CodeFragmentManager
        ServerStub server = ServerStub.getInstance(project);
        server.initialize(project);
    }
}

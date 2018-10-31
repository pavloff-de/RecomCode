package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import de.pavloff.pycharm.plugin.server_stub.ServerStub;
import org.jetbrains.annotations.NotNull;

/** Plugin part which extends the IntelliJ Platform core functionality, see https://goo.gl/eAgPnz
 * It creates GUI panel for recommendations.
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under <extensions ...></extensions>.
 */
public class RecomCodeExtension implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RecomCodeManager manager = RecomCodeManager.getInstance(project);
        toolWindow.getComponent().add(manager.initView(project));

        // ServerStub replaces previous usage of CodeFragmentManager
        ServerStub server = ServerStub.getInstance(project);
        server.initialize(project);
    }
}

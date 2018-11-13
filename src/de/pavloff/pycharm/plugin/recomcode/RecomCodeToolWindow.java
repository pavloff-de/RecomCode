package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/** Plugin code which extends the IntelliJ Platform core functionality,
 * see https://goo.gl/eAgPnz
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under
 * <extensions ...></extensions>.
 * More info: https://www.jetbrains.org/intellij/sdk/docs/user_interface_components/tool_windows.html
 * It adds GUI panel for recommendations to workspace
 */
public class RecomCodeToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RecomCodeManager manager = RecomCodeManager.getInstance(project);
        toolWindow.getComponent().add(manager.getToolWindowComponent());
    }
}

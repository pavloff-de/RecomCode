package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import de.pavloff.pycharm.plugin.server_stub.ServerStub;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;      // output in In ${idea.system.path}/log/idea.log
import org.picocontainer.MutablePicoContainer;

import java.awt.*;


/** Plugin part which extends the IntelliJ Platform core functionality, see https://goo.gl/eAgPnz
 * It creates GUI panel for recommendations.
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under <extensions ...></extensions>.
 * More info: https://www.jetbrains.org/intellij/sdk/docs/user_interface_components/tool_windows.html
 */
public class RecomCodeToolWindow implements ToolWindowFactory {

    private static Logger logger = Logger.getInstance(RecomCodeManager.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.getComponent().add(createRecommenderPanel(project));
    }


    public static Component createRecommenderPanel(@NotNull Project project) {
        RecomCodeManager manager = RecomCodeManager.getInstance(project);

        MutablePicoContainer container = (MutablePicoContainer) project.getPicoContainer();
        logger.info("(later) MutablePicoContainer instance is: " + container.toString());

        return manager.initView(project);
    }

}

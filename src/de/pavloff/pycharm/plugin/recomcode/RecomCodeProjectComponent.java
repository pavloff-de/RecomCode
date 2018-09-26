package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeFragmentManager;
import de.pavloff.pycharm.core.worker.AprioriWorker;
import de.pavloff.pycharm.core.worker.KeywordWorker;
import de.pavloff.pycharm.yaml.YamlLoader;
import org.jetbrains.annotations.NotNull;

public class RecomCodeProjectComponent implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RecomCodeManager manager = RecomCodeManager.getInstance(project);
        toolWindow.getComponent().add(manager.initView(project));

        CodeFragmentLoader loader = new YamlLoader();
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.addWorker(new KeywordWorker(loader));
        recommender.addWorker(new AprioriWorker(loader));
    }
}

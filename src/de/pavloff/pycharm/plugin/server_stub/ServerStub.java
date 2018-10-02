package de.pavloff.pycharm.plugin.server_stub;

import com.intellij.openapi.project.Project;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentManager;

import java.util.LinkedHashSet;

/** Class which wraps all calls to the "core" part.
 *  todo: 1. Extract interface, 2. add additional implementation with gRPC
 *
 */
public class ServerStub {

    static private  ServerStub stub;
    private Project project;

    public static ServerStub getInstance(Project project) {
        // var stub = project.getComponent(ServerStub.class);
        if (stub == null)
            stub = new ServerStub();
        stub.project = project;
        return stub;
    }


    public void initialize() {
        CodeFragmentManager recommender = project.getComponent(CodeFragmentManager.class);
        recommender.initialize();
    }

    public void onInput(String input) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.onInput(input);
    }

    public LinkedHashSet<CodeFragment> getRecomputedRecommendations() {
        var recommender = CodeFragmentManager.getInstance(project);
        return recommender.getRecomputedRecommendations();
    }

    public void codeFragmentSelected(CodeFragment fragment) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.codeFragmentSelected(fragment);
    }

}

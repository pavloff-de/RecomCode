package de.pavloff.pycharm.plugin.server_stub;

import com.intellij.openapi.project.Project;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentManager;

import java.util.ArrayList;
import java.util.List;

/** Class which raps all calls to the "core" part.
 *  todo: 1. Extract interface
 *
 */
public class ServerStub {

    static private  ServerStub stub;
    private CodeFragmentManager recommender;
    private Project project;

    public ServerStub() {
        // todo later: create a server instance
    }

    public static ServerStub getInstance(Project project) {
        // var stub = project.getComponent(ServerStub.class);
        if (stub == null)
            stub = new ServerStub();
        stub.project = project;
        return stub;
    }


    public void initialize() {
        recommender = project.getComponent(CodeFragmentManager.class);
        recommender.initialize();
    }

    public void onInput(String input) {
        recommender.onInput(input);
    }

    private List<CodeFragmentListenerStub> codeFragmentListenerStubs = new ArrayList<CodeFragmentListenerStub>();

    public void addCodeFragmentListener(CodeFragmentListenerStub listener) {
        codeFragmentListenerStubs.add(listener);
    }

    public void codeFragmentSelected(CodeFragment fragment) {
        recommender.codeFragmentSelected(fragment);
    }

}

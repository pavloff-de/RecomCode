package de.pavloff.pycharm.plugin.server_stub;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface ServerStub {
    // todo: think whether we need a separate instance per project? Not good for RPC, as we have only 1 server process

    // instantiate the alternative class ServerStubWithRPC for RPC
    ServerStub stub = new ServerStubNoRPC();
    // static ServerStub stub = new ServerStubWithRPC();

    // Currently, there is only 1 instance for all projects!
    public static ServerStub getInstance(Project project) {
        return stub;
    }

    void initialize(Project project);

    void onInput(String input);

    LinkedHashSet<CodeFragment> getRecomputedRecommendations();

    void codeFragmentSelected(CodeFragment fragment);

    void dataframeSelected(String tableName, TableModel table);

    void cellSelected(int row, int column);

    void cellsSelected(List<Pair<Integer, Integer>> cells);

    void rowSelected(int row);

    void columnSelected(int column);

    void codeVariables(Map<String, CodeVariable> variables);
}

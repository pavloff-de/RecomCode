package de.pavloff.pycharm.plugin.server_stub;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentManager;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** Class which wraps all plugin/varviewer calls to the "core" part, and uses the {@link CodeFragmentManager} directly
 *
 */
public class ServerStubNoRPC implements ServerStub {

    protected Project project;


    @Override
    public void initialize(Project project) {
        this.project = project;
        CodeFragmentManager recommender = project.getComponent(CodeFragmentManager.class);
        recommender.initialize();
    }

    // ======================
    // Methods for the main plugin

    @Override
    public void onInput(String input) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.onInput(input);
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecomputedRecommendations() {
        var recommender = CodeFragmentManager.getInstance(project);
        return recommender.getRecomputedRecommendations();
    }

    @Override
    public void codeFragmentSelected(CodeFragment fragment) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.codeFragmentSelected(fragment);
    }


    // ======================
    // Methods for varviewer

    @Override
    public void dataframeSelected(String tableName, TableModel table) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.dataframeSelected(tableName, table);
    }

    @Override
    public void cellSelected(int row, int column) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.cellSelected(row, column);
    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.cellsSelected(cells);
    }

    @Override
    public void rowSelected(int row) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.rowSelected(row);
    }

    @Override
    public void columnSelected(int column) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.columnSelected(column);
    }

    @Override
    public void codeVariables(Map<String, CodeVariable> variables) {
        var recommender = CodeFragmentManager.getInstance(project);
        recommender.codeVariables(variables);
    }

}


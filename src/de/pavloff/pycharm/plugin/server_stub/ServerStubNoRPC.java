package de.pavloff.pycharm.plugin.server_stub;

import com.intellij.openapi.diagnostic.Logger;
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

    private static Logger logger = Logger.getInstance(ServerStubNoRPC.class);


    @Override
    public void initialize(Project project) {
        logger.debug("initializing ServerStub..");
        this.project = project;
        // CodeFragmentManager defaultCodeFragmentManager = new CodeFragmentManager();
        // CodeFragmentManager recommender = project.getComponent(CodeFragmentManager.class, defaultCodeFragmentManager);
        CodeFragmentManager recommender = project.getComponent(CodeFragmentManager.class);
        recommender.initialize();
        logger.debug("ServerStub initialized");
    }

    // ======================
    // Methods for the main plugin

    @Override
    public void onInput(String input) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onInput(input);
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        return recommender.getRecommendations();
    }

    @Override
    public void onCodeFragment(CodeFragment fragment) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onCodeFragment(fragment);
    }


    // ======================
    // Methods for varviewer

    @Override
    public void onDataframe(String tableName, TableModel table) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onDataframe(tableName, table);
    }

    @Override
    public void onCell(int row, int column) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onCell(row, column);
    }

    @Override
    public void onCells(List<Pair<Integer, Integer>> cells) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onCells(cells);
    }

    @Override
    public void onRow(int row) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onRow(row);
    }

    @Override
    public void onColumn(int column) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onColumn(column);
    }

    @Override
    public void onSourcecode(String code) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onSourcecode(code);
    }

    @Override
    public void onVariables(Map<String, CodeVariable> variables) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(project);
        recommender.onVariables(variables);
    }

}


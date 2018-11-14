package de.pavloff.pycharm.plugin.serverstub;

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

/** Class which wraps all plugin/varviewer calls to the "core" part, and
 * uses the {@link CodeFragmentManager} directly
 *
 */
public class ServerStubNoRPC implements ServerStub {

    private Project openedProject;

    private static Logger logger = Logger.getInstance(ServerStubNoRPC.class);

    @Override
    public void initialize(Project project) {
        logger.debug("initializing ServerStub..");
        openedProject = project;

        CodeFragmentManager recommender = project.getComponent(CodeFragmentManager.class);
        recommender.initialize();

        logger.debug("ServerStub initialized");
    }

    @Override
    public void onInput(String input) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onInput(input);
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        return recommender.getRecommendations();
    }

    @Override
    public void onCodeFragment(CodeFragment fragment) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onCodeFragment(fragment);
    }

    @Override
    public void onDataframe(String tableName, TableModel table) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onDataframe(tableName, table);
    }

    @Override
    public void onCell(int row, int column) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onCell(row, column);
    }

    @Override
    public void onCells(List<Pair<Integer, Integer>> cells) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onCells(cells);
    }

    @Override
    public void onRow(int row) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onRow(row);
    }

    @Override
    public void onColumn(int column) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onColumn(column);
    }

    @Override
    public void onSourcecode(String code) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onSourcecode(code);
    }

    @Override
    public void onVariables(Map<String, CodeVariable> variables) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.onVariables(variables);
    }
}


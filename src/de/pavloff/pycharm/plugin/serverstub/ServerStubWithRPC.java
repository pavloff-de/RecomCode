package de.pavloff.pycharm.plugin.serverstub;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** Class which will use gRPC and communicate with a remote recommender engine (server)
 */
public class ServerStubWithRPC implements ServerStub {

    private Project openedProject;

    private static Logger logger = Logger.getInstance(ServerStubWithRPC.class);


    @Override
    public void initialize(Project project) {
        logger.debug("initializing ServerStub..");
        openedProject = project;

        // todo: initialize / check connection to recommender engine

        logger.debug("ServerStub initialized");
    }

    @Override
    public void onInput(String input) {
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        return new LinkedHashSet<>(0);
    }

    @Override
    public void onCodeFragment(CodeFragment fragment) {
    }

    @Override
    public void onDataframe(String tableName, TableModel table) {
    }

    @Override
    public void onCell(int row, int column) {
    }

    @Override
    public void onCells(List<Pair<Integer, Integer>> cells) {
    }

    @Override
    public void onRow(int row) {
    }

    @Override
    public void onColumn(int column) {
    }

    @Override
    public void onSourcecode(String code) {
    }

    @Override
    public void onVariables(Map<String, CodeVariable> variables) {
    }
}


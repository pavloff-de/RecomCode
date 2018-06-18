package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.SimpleWorker;
import de.pavloff.pycharm.core.worker.Worker;
import de.pavloff.pycharm.yaml.YamlLoader;

import javax.swing.table.TableModel;
import java.util.LinkedList;
import java.util.List;

public class CodeFragmentManager implements Worker {

    //TODO: make configurable
    private CodeFragmentLoader loader = new YamlLoader();
    private Worker worker = new SimpleWorker(loader);

    private List<CodeFragmentListener> codeFragmentListeners = new LinkedList<>();
    private List<CodeParamListener> codeParamListeners = new LinkedList<>();

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    public void addCodeFragmentListener(CodeFragmentListener listener) {
        codeFragmentListeners.add(listener);
    }

    public void addCodeParamListener(CodeParamListener listener) {
        codeParamListeners.add(listener);
    }

    private void returnRecommendations(List<CodeFragment> recommendation) {
        for (CodeFragmentListener listener : codeFragmentListeners) {
            listener.onOutput(recommendation);
        }
    }

    @Override
    public String workerName() {
        return "Worker manager";
    }

    @Override
    public String description() {
        return "Just a proxy. Do not use.";
    }

    @Override
    public void onInput(String input) {
        worker.onInput(input);
        returnRecommendations(worker.getRecommendation());
    }

    @Override
    public void dataframeSelected(TableModel table) {
        worker.dataframeSelected(table);
        returnRecommendations(worker.getRecommendation());
    }

    @Override
    public void cellSelected(int row, int column) {
        worker.cellSelected(row, column);
        returnRecommendations(worker.getRecommendation());
    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {
        worker.cellsSelected(cells);
        returnRecommendations(worker.getRecommendation());
    }

    @Override
    public void rowSelected(int row) {
        worker.rowSelected(row);
        returnRecommendations(worker.getRecommendation());
    }

    @Override
    public void columnSelected(int column) {
        worker.columnSelected(column);
        returnRecommendations(worker.getRecommendation());
    }

    @Override
    public List<CodeFragment> getRecommendation() {
        return null;
    }

    @Override
    public void selectedCodeFragment(CodeFragment fragment) {
        worker.selectedCodeFragment(fragment);
    }
}

package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.AprioriWorker;
import de.pavloff.pycharm.core.worker.KeywordWorker;
import de.pavloff.pycharm.core.worker.Worker;
import de.pavloff.pycharm.yaml.YamlLoader;

import javax.swing.table.TableModel;
import java.util.*;

public class CodeFragmentManager extends Worker {

    private Map<String, Worker> workers = new HashMap<>();

    private List<CodeFragmentListener> codeFragmentListeners = new LinkedList<>();

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    public void addCodeFragmentListener(CodeFragmentListener listener) {
        codeFragmentListeners.add(listener);
    }

    private void returnRecommendations() {
        CodeFragment.FragmentSorter sorter = new CodeFragment.FragmentSorter();

        LinkedHashSet<CodeFragment> recommendation = getSelectedCodeFragments();
        Iterator<CodeFragment> it = recommendation.iterator();
        int rank = recommendation.size();

        while (it.hasNext()) {
            sorter.add(it.next(), rank);
            rank--;
        }

        for (Worker worker : workers.values()) {
            recommendation = worker.getRecommendations();
            it = recommendation.iterator();
            rank = recommendation.size();

            while (it.hasNext()) {
                sorter.add(it.next(), rank);
                rank--;
            }
        }

        recommendation = sorter.sortFragments();

        LinkedHashSet<CodeFragment> withVariables = new LinkedHashSet<>();
        for (CodeFragment fragment : recommendation) {

            List<CodeFragment> fragmentWithVariables = fragment.getWithVariables(getMyVariables());
            if (fragmentWithVariables.size() != 0) {
                withVariables.addAll(fragmentWithVariables);
            } else {
                withVariables.add(fragment);
            }
        }

        for (CodeFragmentListener listener : codeFragmentListeners) {
            listener.onOutput(withVariables);
        }
    }

    @Override
    public void initialize() {
        CodeFragmentLoader loader = new YamlLoader();

        Worker kw = new KeywordWorker(loader);
        workers.put(kw.workerName(), kw);

        Worker aw = new AprioriWorker();
        workers.put(aw.workerName(), aw);
    }

    @Override
    public String workerName() {
        return "Worker manager";
    }

    @Override
    public String description() {
        return "Proxy for multiple worker.";
    }

    @Override
    protected void inputProcessing(String input) {
        for (Worker worker : workers.values()) {
            worker.onInput(input);
        }
        returnRecommendations();
    }

    @Override
    protected void dataframeProcessing(String tableName, TableModel table) {
        for (Worker worker : workers.values()) {
            worker.onDataframe(tableName, table);
        }

        returnRecommendations();
    }

    @Override
    protected void cellProcessing(int row, int column) {
        for (Worker worker : workers.values()) {
            worker.onCell(row, column);
        }

        returnRecommendations();
    }

    @Override
    protected void cellsprocessing(List<Pair<Integer, Integer>> cells) {
        for (Worker worker : workers.values()) {
            worker.onCells(cells);
        }
        returnRecommendations();
    }

    @Override
    protected void rowProcessing(int row) {
        for (Worker worker : workers.values()) {
            worker.onRow(row);
        }

        returnRecommendations();
    }

    @Override
    protected void columnProcessing(int column) {
        for (Worker worker : workers.values()) {
            worker.onColumn(column);
        }

        returnRecommendations();
    }

    @Override
    protected void sourcecodeProcessing(String code) {
        for (Worker worker : workers.values()) {
            worker.onSourcecode(code);
        }
    }

    @Override
    protected void variablesProcessing(Map<String, CodeVariable> variables) {
        for (Worker worker : workers.values()) {
            worker.onVariables(variables);
        }
    }

    @Override
    protected void codeFragmentProcessing(CodeFragment fragment) {
        for (Worker worker : workers.values()) {
            worker.onCodeFragment(fragment);
        }
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        return null;
    }
}

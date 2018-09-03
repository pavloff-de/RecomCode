package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.AprioriWorker;
import de.pavloff.pycharm.core.worker.HistoryWorker;
import de.pavloff.pycharm.core.worker.KeywordWorker;
import de.pavloff.pycharm.core.worker.Worker;
import de.pavloff.pycharm.yaml.YamlLoader;

import javax.swing.table.TableModel;
import java.util.*;

public class CodeFragmentManager implements Worker {

    // TODO: make configurable
    private CodeFragmentLoader loader = new YamlLoader();

    private Worker worker1 = new KeywordWorker(loader);
    private Worker worker2 = new AprioriWorker(loader);
    private Worker worker3 = new HistoryWorker(loader);

    private List<CodeFragmentListener> codeFragmentListeners = new LinkedList<>();

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    public void addCodeFragmentListener(CodeFragmentListener listener) {
        codeFragmentListeners.add(listener);
    }

    public CodeFragmentLoader getLoader() {
        return loader;
    }

    private void returnRecommendations() {
        CodeFragment.FragmentSorter sorter = new CodeFragment.FragmentSorter();

        LinkedHashSet<CodeFragment> recommendation1 = worker1.getRecommendation();
        Iterator<CodeFragment> it = recommendation1.iterator();
        int r = recommendation1.size();
        while (it.hasNext()) {
            sorter.add(it.next(), r);
            r--;
        }

        LinkedHashSet<CodeFragment> recommendation2 = worker2.getRecommendation();
        it = recommendation2.iterator();
        r = recommendation2.size();
        while (it.hasNext()) {
            sorter.add(it.next(), r);
            r--;
        }

        LinkedHashSet<CodeFragment> recommendation3 = worker3.getRecommendation();
        it = recommendation3.iterator();
        r = recommendation3.size();
        while (it.hasNext()) {
            sorter.add(it.next(), r);
            r--;
        }

        LinkedHashSet<CodeFragment> recommendation = sorter.sortFragments();
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
        return "Just a proxy. Do not use as a worker.";
    }

    @Override
    public void onInput(String input) {
        worker1.onInput(input);
        worker2.onInput(input);
        worker3.onInput(input);
        returnRecommendations();
    }

    @Override
    public void dataframeSelected(TableModel table) {
        worker1.dataframeSelected(table);
        worker2.dataframeSelected(table);
        worker3.dataframeSelected(table);
        returnRecommendations();
    }

    @Override
    public void cellSelected(int row, int column) {
        worker1.cellSelected(row, column);
        worker2.cellSelected(row, column);
        worker3.cellSelected(row, column);
        returnRecommendations();
    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {
        worker1.cellsSelected(cells);
        worker2.cellsSelected(cells);
        worker3.cellsSelected(cells);
        returnRecommendations();
    }

    @Override
    public void rowSelected(int row) {
        worker1.rowSelected(row);
        worker2.rowSelected(row);
        worker3.rowSelected(row);
        returnRecommendations();
    }

    @Override
    public void columnSelected(int column) {
        worker1.columnSelected(column);
        worker2.columnSelected(column);
        worker3.columnSelected(column);
        returnRecommendations();
    }

    @Override
    public void codeFragmentSelected(CodeFragment fragment) {
        worker1.codeFragmentSelected(fragment);
        worker2.codeFragmentSelected(fragment);
        worker3.codeFragmentSelected(fragment);
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendation() {
        return null;
    }
}

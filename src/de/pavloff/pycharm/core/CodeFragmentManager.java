package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.AprioriWorker;
import de.pavloff.pycharm.core.worker.KeywordWorker;
import de.pavloff.pycharm.core.worker.Worker;
import de.pavloff.pycharm.yaml.YamlLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableModel;
import java.util.*;

public class CodeFragmentManager extends Worker {

    private Map<String, Worker> workers = new HashMap<>();

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    @NotNull
    public LinkedHashSet<CodeFragment> getRecommendations() {
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

        LinkedHashSet<Pair<Integer, CodeFragment>> ratedRecommendations = sorter.getSortedFragmentsWithRating();

        for (Pair<Integer, CodeFragment> ratedFragment : ratedRecommendations) {
            List<CodeFragment> fragmentWithVariables = ratedFragment.second.getWithVariables(getMyVariables());
            int numOfVariables = fragmentWithVariables.size();

            if (numOfVariables != 0) {
                for (CodeFragment fragmentWithVariable : fragmentWithVariables) {
                    sorter.add(fragmentWithVariable, ratedFragment.first + numOfVariables);
                }
                sorter.remove(ratedFragment.second);
            }
        }

        return sorter.getSortedFragments();
    }

    private Boolean initialized = false;

    @Override
    public void initialize() {
        if (initialized) return;

        CodeFragmentLoader loader = new YamlLoader();

        Worker kw = new KeywordWorker(loader);
        workers.put(kw.workerName(), kw);

        Worker aw = new AprioriWorker();
        workers.put(aw.workerName(), aw);

        initialized = true;
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
        // returnRecommendations();
    }

    @Override
    protected void dataframeProcessing(String tableName, TableModel table) {
        for (Worker worker : workers.values()) {
            worker.onDataframe(tableName, table);
        }

        // returnRecommendations();
    }

    @Override
    protected void cellProcessing(int row, int column) {
        for (Worker worker : workers.values()) {
            worker.onCell(row, column);
        }

        // returnRecommendations();
    }

    @Override
    protected void cellsprocessing(List<Pair<Integer, Integer>> cells) {
        for (Worker worker : workers.values()) {
            worker.onCells(cells);
        }
        // returnRecommendations();
    }

    @Override
    protected void rowProcessing(int row) {
        for (Worker worker : workers.values()) {
            worker.onRow(row);
        }

        // returnRecommendations();
        // hint: for each call of rowSelected outside core.*, insert the following afterwards:
        //            var recomCodeManager = RecomCodeManager.getInstance(openedProject);
        //            recomCodeManager.updateAndDisplayRecommendations();
    }

    @Override
    protected void columnProcessing(int column) {
        for (Worker worker : workers.values()) {
            worker.onColumn(column);
        }

        // returnRecommendations();
        // hint: for each call of rowSelected outside core.*, insert the following afterwards:
        //            var recomCodeManager = RecomCodeManager.getInstance(openedProject);
        //            recomCodeManager.updateAndDisplayRecommendations();
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
}

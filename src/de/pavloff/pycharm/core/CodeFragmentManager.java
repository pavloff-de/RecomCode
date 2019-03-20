package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.AprioriWorker;
import de.pavloff.pycharm.core.worker.KeywordWorker;
import de.pavloff.pycharm.core.worker.Worker;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableModel;
import java.util.*;

/** Main implementation of {@link Worker}
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under
 * <project-components>.
 * It combines the recommendations of {@link AprioriWorker}, {@link KeywordWorker}
 * and kind of HistoryWorker integrated in the main class {@link Worker}
 */
public class CodeFragmentManager extends Worker {

    private Map<String, Worker> workers = new HashMap<>();

    public CodeFragmentManager(Project project) {
        initialize(project);
    }

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    /**
     * returns recommendations from all worker
     * recommendations are ranked on the position in a sorted list of each worker
     * fragments are sorted by descending order so first fragment gets the highest rank
     */
    @NotNull
    public LinkedHashSet<CodeFragment> getRecommendations() {
        CodeFragment.FragmentSorter sorter = new CodeFragment.FragmentSorter();

        // HistoryWorker
        rankAndSort(sorter, getSelectedCodeFragments());

        // other Worker
        for (Worker worker : workers.values()) {
            rankAndSort(sorter, worker.getRecommendations());
        }

        LinkedHashSet<Pair<Double, CodeFragment>> ratedRecommendations =
                sorter.getSortedFragmentsWithRating();

        // put variables into fragments
        for (Pair<Double, CodeFragment> ratedFragment : ratedRecommendations) {
            List<CodeFragment> fragmentWithVariables = ratedFragment.second.getWithVariables(getMyVariables());
            int numOfVariables = fragmentWithVariables.size();

            if (numOfVariables != 0) {
                // increasing of rank since matched variable
                for (CodeFragment fragmentWithVariable : fragmentWithVariables) {
                    sorter.add(fragmentWithVariable, ratedFragment.first + numOfVariables);
                }
                sorter.remove(ratedFragment.second);
            }
        }

        return sorter.getSortedFragments();
    }

    private void rankAndSort(CodeFragment.FragmentSorter sorter, LinkedHashSet<CodeFragment> fragments) {
        int rank = fragments.size();
        for (CodeFragment fragment : fragments) {
            sorter.add(fragment, rank);
            rank--;
        }
    }

    private Boolean initialized = false;

    @Override
    public void initialize(Project project) {
        if (initialized) return;
        initialized = true;

        Worker kw = new KeywordWorker();
        kw.initialize(project);
        workers.put(kw.workerName(), kw);

        Worker aw = new AprioriWorker();
        aw.initialize(project);
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
    }

    @Override
    protected void dataframeProcessing(String tableName, TableModel table) {
        for (Worker worker : workers.values()) {
            worker.onDataframe(tableName, table);
        }
    }

    @Override
    protected void cellProcessing(int row, int column) {
        for (Worker worker : workers.values()) {
            worker.onCell(row, column);
        }
    }

    @Override
    protected void cellsProcessing(List<Pair<Integer, Integer>> cells) {
        for (Worker worker : workers.values()) {
            worker.onCells(cells);
        }
    }

    @Override
    protected void rowProcessing(int row) {
        for (Worker worker : workers.values()) {
            worker.onRow(row);
        }
    }

    @Override
    protected void columnProcessing(int column) {
        for (Worker worker : workers.values()) {
            worker.onColumn(column);
        }
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

package de.pavloff.pycharm.core;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.AprioriWorker;
import de.pavloff.pycharm.core.worker.KeywordWorker;
import de.pavloff.pycharm.core.worker.Worker;
import de.pavloff.pycharm.plugin.YamlLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableModel;
import java.util.*;

/** Main implementation of {@link Worker}
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under
 * <project-components>.
 * It combines the recommendations of {@link AprioriWorker}, {@link KeywordWorker}
 * and kind of HistoryWorker integrated in the main class {@link Worker}
 */
@State(name = "CodeFragmentManager", storages = {@Storage("selectedCodeFragments.xml")})
public class CodeFragmentManager extends Worker implements PersistentStateComponent<CodeFragmentManager.State> {

    private Project openedProject;

    private Map<String, Worker> workers = new HashMap<>();

    private HashMap<String, Boolean> fragmentFilter = new HashMap<>();

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
        rankAndSort(sorter, filterFragments(getUniqueSelectedCodeFragments()));

        // other Worker
        for (Worker worker : workers.values()) {
            rankAndSort(sorter, filterFragments(worker.getRecommendations()));
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

        return sorter.getSortedFragments(0);
    }

    /**
     * returns a group filter for fragments
     */
    public HashMap<String, Boolean> getFragmentFilter() {
        return fragmentFilter;
    }

    /**
     * changes a group filter for fragments
     */
    public void putFragmentFilter(String group, Boolean enabled) {
        fragmentFilter.put(group, enabled);
    }

    /**
     * applies a group filter on fragments
     */
    private LinkedHashSet<CodeFragment> filterFragments(LinkedHashSet<CodeFragment> fragments) {
        if (fragmentFilter == null || fragmentFilter.size() == 0) {
            return fragments;
        }

        LinkedHashSet<CodeFragment> filteredFragments = new LinkedHashSet<>();
        if (fragments == null) {
            return filteredFragments;
        }

        for (CodeFragment fragment : fragments) {
            for (String subgroup : fragment.getSubgroup()) {
                if (fragmentFilter.containsKey(subgroup) && fragmentFilter.get(subgroup)) {
                    filteredFragments.add(fragment);
                    break;
                }
            }
        }

        return filteredFragments;
    }

    private void rankAndSort(CodeFragment.FragmentSorter sorter, LinkedHashSet<CodeFragment> fragments) {
        int rank = Math.max(5, fragments.size());
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

        openedProject = project;

        CodeFragmentLoader loader = YamlLoader.getInstance(openedProject);
        for (CodeFragment codeFragment : loader.getCodeFragments()) {
            for (String subgroup : codeFragment.getSubgroup()) {
                putFragmentFilter(subgroup, true);
            }
        }
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
    public void onCodeFragment(CodeFragment fragment) {
        // do not save fragment generated with variables
        Map<String, CodeFragment> fragments = YamlLoader.getInstance(openedProject).getCodeFragmentsWithID();
        super.onCodeFragment(fragments.get(fragment.getRecID()));
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
            worker.onSourceCode(code);
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
    @Nullable
    @Override
    public State getState() {

        // save history
        List<String> fragmentIds = new ArrayList<>();
        for (CodeFragment selectedCodeFragment : getSelectedCodeFragments()) {
            fragmentIds.add(0, selectedCodeFragment.getRecID());
        }
        return new State(fragmentIds.toArray(new String[0]), fragmentFilter);
    }

    @Override
    public void loadState(@NotNull State state) {
        String[] fragmentIds = state.historyWorkerState;
        if (fragmentIds == null || fragmentIds.length == 0) {
            return;
        }
        YamlLoader loader = YamlLoader.getInstance(openedProject);
        Map<String, CodeFragment> fragments = loader.getCodeFragmentsWithID();

        for (String fragmentId : fragmentIds) {
            CodeFragment fragment = fragments.get(fragmentId);
            if (fragment != null) {
                addCodeFragmentToHistory(fragment);
            }
        }

        fragmentFilter.putAll(state.filterEnabled);
    }

    /** State for Worker
     * It persists the history of user input between restarts
     */
    public static final class State {

        public String[] historyWorkerState;

        public HashMap<String, Boolean> filterEnabled;

        public State() {
            historyWorkerState = new String[0];
            filterEnabled = new HashMap<>();
        }

        public State(String[] toPersist, HashMap<String, Boolean> filterToPersis) {
            historyWorkerState = toPersist;
            filterEnabled = filterToPersis;
        }
    }
}

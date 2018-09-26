package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.Worker;

import javax.swing.table.TableModel;
import java.util.*;

public class CodeFragmentManager implements Worker {

    private Map<String, Worker> workers = new HashMap<>();

    private List<CodeFragmentListener> codeFragmentListeners = new LinkedList<>();

    private List<CodeFragment> selectedCodeFragmets = new LinkedList<>();

    private TableModel selectedDataframe;

    private Map<String, List<CodeVariable>> myVariables = new HashMap<>();

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    public void addCodeFragmentListener(CodeFragmentListener listener) {
        codeFragmentListeners.add(listener);
    }

    public void addWorker(Worker worker) {
        workers.put(worker.workerName(), worker);
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

            List<CodeFragment> fragmentWithVariables = fragment.getWithVariables(myVariables);
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
    public String workerName() {
        return "Worker manager";
    }

    @Override
    public String description() {
        return "Proxy for multiple worker.";
    }

    private void addVariable(String param, String type, String varName, String value, String moduleName) {
        List<CodeVariable> vars;

        if (myVariables.containsKey(param)) {
            vars = myVariables.get(param);
        } else {
            vars = new LinkedList<>();
            myVariables.put(param, vars);
        }

        vars.add(new CodeVariable.Builder()
                .setType(type).setName(varName).setValue(value).setModuleName(moduleName).build());
    }

    @Override
    public void onInput(String input) {
        for (Worker worker : workers.values()) {
            worker.onInput(input);
        }
        returnRecommendations();
    }

    @Override
    public void dataframeSelected(String tableName, TableModel table) {
        for (Worker worker : workers.values()) {
            worker.dataframeSelected(tableName, table);
        }

        selectedDataframe = table;
        addVariable("dataframe", "DataFrame", tableName, null, null);
        returnRecommendations();
    }

    @Override
    public void cellSelected(int row, int column) {
        for (Worker worker : workers.values()) {
            worker.cellSelected(row, column);
        }

        addVariable("row_index", "int", "row_index", String.valueOf(row), null);
        addVariable("column_index", "int", "column_index", String.valueOf(column), null);

        if (selectedDataframe != null) {
            addVariable("column_name", "str", "column_name", selectedDataframe.getColumnName(column), null);
        }

        returnRecommendations();
    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {
        for (Worker worker : workers.values()) {
            worker.cellsSelected(cells);
        }
        returnRecommendations();
    }

    @Override
    public void rowSelected(int row) {
        for (Worker worker : workers.values()) {
            worker.rowSelected(row);
        }

        addVariable("row_index", "int", "row_index", String.valueOf(row), null);

        returnRecommendations();
    }

    @Override
    public void columnSelected(int column) {
        for (Worker worker : workers.values()) {
            worker.columnSelected(column);
        }

        addVariable("column_index", "int", "column_index", String.valueOf(column), null);

        if (selectedDataframe != null) {
            addVariable("column_name", "str", "column_name", selectedDataframe.getColumnName(column), null);
        }

        returnRecommendations();
    }

    @Override
    public void codeFragmentSelected(CodeFragment fragment) {
        for (Worker worker : workers.values()) {
            worker.codeFragmentSelected(fragment);
        }

        selectedCodeFragmets.removeIf(k -> k.equals(fragment));
        selectedCodeFragmets.add(0, fragment);
    }

    @Override
    public void sourceCode(String code) {
        for (Worker worker : workers.values()) {
            worker.sourceCode(code);
        }
    }

    @Override
    public void codeVariables(Map<String, CodeVariable> variables) {
        for (Worker worker : workers.values()) {
            worker.codeVariables(variables);
        }

        for (CodeVariable var : variables.values()) {
            if (var.getType().equals("module")) {
                addVariable(var.getType(), var.getType(), var.getName(), var.getValue(), var.getModuleName());
            }
        }
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        return null;
    }

    public LinkedHashSet<CodeFragment> getSelectedCodeFragments() {
        return getSelectedCodeFragments(5);
    }

    public LinkedHashSet<CodeFragment> getSelectedCodeFragments(int numOfFragments) {
        LinkedHashSet<CodeFragment> lastFragments = new LinkedHashSet<>();

        if (selectedCodeFragmets.size() != 0) {
            // find last numOfFragments fragments
            ListIterator<CodeFragment> it = selectedCodeFragmets.listIterator(Math.max(selectedCodeFragmets.size() - numOfFragments, 1) - 1);
            while (it.hasNext()) {
                lastFragments.add(it.next());
            }
        }

        return lastFragments;
    }
}

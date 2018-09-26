package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.*;

public abstract class Worker {

    private Map<String, List<CodeVariable>> myVariables = new HashMap<>();

    private List<CodeFragment> selectedCodeFragments = new LinkedList<>();

    private TableModel selectedDataframe;

    public abstract void initialize();

    public abstract String workerName();

    public abstract String description();

    public Map<String, List<CodeVariable>> getMyVariables() {
        return myVariables;
    }

    public TableModel getSelectedDataframe() {
        return selectedDataframe;
    }

    public LinkedHashSet<CodeFragment> getSelectedCodeFragments() {
        return getSelectedCodeFragments(5);
    }

    public LinkedHashSet<CodeFragment> getSelectedCodeFragments(int numOfFragments) {
        LinkedHashSet<CodeFragment> lastFragments = new LinkedHashSet<>();

        if (selectedCodeFragments.size() != 0) {
            // find last numOfFragments fragments
            ListIterator<CodeFragment> it = selectedCodeFragments.listIterator(Math.max(selectedCodeFragments.size() - numOfFragments, 1) - 1);
            while (it.hasNext()) {
                lastFragments.add(it.next());
            }
        }

        return lastFragments;
    }

    public abstract LinkedHashSet<CodeFragment> getRecommendations();

    public void onInput(String input) {
        inputProcessing(input);
    }

    public void onDataframe(String tableName, TableModel table) {
        selectedDataframe = table;
        addVariable("dataframe", "DataFrame", tableName, null, null);

        dataframeProcessing(tableName, table);
    }

    public void onCell(int row, int column) {
        addVariable("row_index", "int", "row_index", String.valueOf(row), null);
        addVariable("column_index", "int", "column_index", String.valueOf(column), null);

        if (selectedDataframe != null) {
            addVariable("column_name", "str", "column_name", selectedDataframe.getColumnName(column), null);
        }

        cellProcessing(row, column);
    }

    public void onCells(List<Pair<Integer, Integer>> cells) {
        cellsprocessing(cells);
    }

    public void onRow(int row) {
        addVariable("row_index", "int", "row_index", String.valueOf(row), null);

        rowProcessing(row);
    }

    public void onColumn(int column) {
        addVariable("column_index", "int", "column_index", String.valueOf(column), null);

        if (selectedDataframe != null) {
            addVariable("column_name", "str", "column_name", selectedDataframe.getColumnName(column), null);
        }

        columnProcessing(column);
    }

    public void onSourcecode(String code) {
        sourcecodeProcessing(code);
    }

    public void onVariables(Map<String, CodeVariable> variables) {
        for (CodeVariable var : variables.values()) {
            if (var.getType().equals("module")) {
                addVariable(var.getType(), var.getType(), var.getName(), var.getValue(), var.getModuleName());
            }
        }

        variablesProcessing(variables);
    }

    public void onCodeFragment(CodeFragment fragment) {
        selectedCodeFragments.removeIf(k -> k.equals(fragment));
        selectedCodeFragments.add(0, fragment);

        codeFragmentProcessing(fragment);
    }

    protected void addVariable(String param, String type, String varName, String value, String moduleName) {
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

    protected abstract void inputProcessing(String input);

    protected abstract void dataframeProcessing(String tableName, TableModel table);

    protected abstract void cellProcessing(int row, int column);

    protected abstract void cellsprocessing(List<Pair<Integer, Integer>> cells);

    protected abstract void rowProcessing(int row);

    protected abstract void columnProcessing(int column);

    protected abstract void sourcecodeProcessing(String code);

    protected abstract void variablesProcessing(Map<String, CodeVariable> variables);

    protected abstract void codeFragmentProcessing(CodeFragment fragment);
}

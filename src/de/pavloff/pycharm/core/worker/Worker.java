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
        if (input.endsWith(" ")) {
            // FIXME: during typing all prefixes of a variable will be saved
            // m, my, myV, myVa, myVar
            // implement delay OR save just the last one
            parseVariablesFromInput(input);
        }

        inputProcessing(input);
    }

    protected void parseVariablesFromInput(String input) {
        String[] inputs = input.split(" ");
        int l = inputs.length;

        for (int i = 0; i < l - 1; i++) {
            String k = inputs[i].toLowerCase();

            switch (k) {
                case "dataframe":
                    addDataframeVariable(inputs[i + 1]);

                    break;
                case "column": {
                    String c = inputs[i + 1];
                    if (c.matches("^\\d+$")) {
                        addColumnVariable(Integer.valueOf(c));
                    } else {
                        addColumnNameVariable(c);
                    }

                    break;
                }
                case "row": {
                    String c = inputs[i + 1];
                    if (c.matches("^\\d+$")) {
                        addRowVariable(Integer.valueOf(c));
                    }
                    break;
                }
            }
        }
    }

    public void onDataframe(String tableName, TableModel table) {
        selectedDataframe = table;
        addDataframeVariable(tableName);

        dataframeProcessing(tableName, table);
    }

    public void onCell(int row, int column) {
        addRowVariable(row);
        addColumnVariable(column);

        if (selectedDataframe != null) {
            addColumnNameVariable(selectedDataframe.getColumnName(column));
        }

        cellProcessing(row, column);
    }

    public void onCells(List<Pair<Integer, Integer>> cells) {
        cellsprocessing(cells);
    }

    public void onRow(int row) {
        addRowVariable(row);

        rowProcessing(row);
    }

    public void onColumn(int column) {
        addColumnVariable(column);

        if (selectedDataframe != null) {
            addColumnNameVariable(selectedDataframe.getColumnName(column));
        }

        columnProcessing(column);
    }

    public void onSourcecode(String code) {
        sourcecodeProcessing(code);
    }

    public void onVariables(Map<String, CodeVariable> variables) {
        for (CodeVariable var : variables.values()) {
            if (var.getType().equals("module")) {
                addVariable(var.getType(), var.getType(), var.getName(), var.getModuleName());
            }
        }

        variablesProcessing(variables);
    }

    public void onCodeFragment(CodeFragment fragment) {
        selectedCodeFragments.removeIf(k -> k.equals(fragment));
        selectedCodeFragments.add(0, fragment);

        codeFragmentProcessing(fragment);
    }

    protected void addDataframeVariable(String tableName) {
        addVariable("DataFrame", "dataframe", tableName, null);
    }

    protected void addRowVariable(int row) {
        addVariable( "int", "row_index", String.valueOf(row), null);
    }

    protected void addColumnVariable(int column) {
        addVariable( "int", "column_index", String.valueOf(column), null);
    }

    protected void addColumnNameVariable(String columnName) {
        addVariable("str", "column_name", columnName, null);
    }

    protected void addVariable(String type, String varName, String value, String moduleName) {
        List<CodeVariable> vars;

        if (myVariables.containsKey(varName)) {
            vars = myVariables.get(varName);
            int s = vars.size();
            if (s > 4) {
                vars = vars.subList(s - 5, s - 1);
            }

        } else {
            vars = new LinkedList<>();
        }

        myVariables.put(varName, vars);

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

package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.*;

/** Main class handling the user inputs and making recommendations
 */
public abstract class Worker {

    /**
     * contains variables from context
     */
    private Map<String, List<CodeVariable>> myVariables = new HashMap<>();

    /**
     * history of the selected fragments
     * first entry is always the last selected fragment
     */
    private List<CodeFragment> selectedCodeFragments = new LinkedList<>();

    /**
     * currently opened pandas dataframe
     */
    private TableModel selectedDataframe;

    /**
     * can be used to initialize the worker or additional objects like server connection
     * @param project opened Project
     */
    public abstract void initialize(Project project);

    /**
     * name of the worker
     * can be used in a configuration of the plugin
     */
    public abstract String workerName();

    /**
     * description of the worker
*      can be used in a configuration of the plugin
     */
    public abstract String description();

    protected Map<String, List<CodeVariable>> getMyVariables() {
        return myVariables;
    }

    public TableModel getSelectedDataframe() {
        return selectedDataframe;
    }

    protected LinkedHashSet<CodeFragment> getSelectedCodeFragments() {
        CodeFragment.FragmentSorter sorter = new CodeFragment.FragmentSorter();
        ListIterator<CodeFragment> listIit =
                selectedCodeFragments.listIterator(selectedCodeFragments.size());

        double rank = 1;
        while (listIit.hasPrevious()) {
            sorter.add(listIit.previous(), rank);
            rank /= 2;
        }

        return sorter.getSortedFragments();
    }

    /**
     * returns the list of recommended code fragments
     */
    public abstract LinkedHashSet<CodeFragment> getRecommendations();

    /**
     * handles the user input
     */
    public void onInput(String input) {
        if (input.endsWith(" ")) {
            // FIXME: during typing all prefixes of a variable will be saved
            // m, my, myV, myVa, myVar
            // implement delay OR save just the last one
            parseVariablesFromInput(input);
        }

        inputProcessing(input);
    }

    private void parseVariablesFromInput(String input) {
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

    /**
     * handles the selection of a pandas dataframe
     */
    public void onDataframe(String tableName, TableModel table) {
        selectedDataframe = table;
        addDataframeVariable(tableName);

        dataframeProcessing(tableName, table);
    }

    /**
     * handles the selection of a cell in a pandas dataframe
     */
    public void onCell(int row, int column) {
        addRowVariable(row);
        addColumnVariable(column);

        if (selectedDataframe != null) {
            addColumnNameVariable(selectedDataframe.getColumnName(column));
            addColumnNamesVariable("\"" + selectedDataframe.getColumnName(column) + "\"");
        }

        cellProcessing(row, column);
    }

    /**
     * handles the selection of many cells in a pandas dataframe
     */
    public void onCells(List<Pair<Integer, Integer>> cells) {
        if (cells.size() == 0) {
            return;
        }

        if (selectedDataframe != null) {
            int someSelectedRow = cells.get(0).first;
            ArrayList<String> columnNames = new ArrayList<>();

            for (Pair<Integer, Integer> cell : cells) {
                if (cell.first != someSelectedRow) {
                    // run over one row
                    continue;
                }
                columnNames.add(selectedDataframe.getColumnName(cell.second));
            }
            addColumnNamesVariable("[\"" + String.join("\", \"", columnNames) + "\"]");
        }

        cellsProcessing(cells);
    }

    /**
     * handles the selection of a row in a pandas dataframe
     */
    public void onRow(int row) {
        addRowVariable(row);

        rowProcessing(row);
    }

    /**
     * handles the selection of a column in a pandas dataframe
     */
    public void onColumn(int column) {
        addColumnVariable(column);

        if (selectedDataframe != null) {
            addColumnNameVariable(selectedDataframe.getColumnName(column));
        }

        columnProcessing(column);
    }

    /**
     * handles the source code
     */
    public void onSourcecode(String code) {
        sourcecodeProcessing(code);
    }

    /**
     * handles the variables from context
     */
    public void onVariables(Map<String, CodeVariable> variables) {
        for (CodeVariable var : variables.values()) {
            if (var.getType().equals("module")) {
                addVariable(var.getType(), var.getType(), var.getName(), var.getModuleName());
            }
        }

        variablesProcessing(variables);
    }

    private void addDataframeVariable(String tableName) {
        addVariable("DataFrame", "dataframe", tableName, null);
    }

    private void addRowVariable(int row) {
        addVariable( "int", "rowIndex", String.valueOf(row), null);
    }

    private void addColumnVariable(int column) {
        addVariable( "int", "columnIndex", String.valueOf(column), null);
    }

    private void addColumnNameVariable(String columnName) {
        addVariable("str", "columnName", columnName, null);
    }

    private void addColumnNamesVariable(String columnNames) {
        addVariable("list", "columnNames", columnNames, null);
    }

    private void addVariable(String type, String varName, String value, String moduleName) {
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

    /**
     * handles the selection of a code fragment
     */
    public void onCodeFragment(CodeFragment fragment) {
        selectedCodeFragments.add(0, fragment);

        codeFragmentProcessing(fragment);
    }

    // protected methods which can be used to extend the functionality of worker //

    protected abstract void inputProcessing(String input);

    protected abstract void dataframeProcessing(String tableName, TableModel table);

    protected abstract void cellProcessing(int row, int column);

    protected abstract void cellsProcessing(List<Pair<Integer, Integer>> cells);

    protected abstract void rowProcessing(int row);

    protected abstract void columnProcessing(int column);

    protected abstract void sourcecodeProcessing(String code);

    protected abstract void variablesProcessing(Map<String, CodeVariable> variables);

    protected abstract void codeFragmentProcessing(CodeFragment fragment);
    // end of protected methods which can be used to extend the functionality of worker //
}

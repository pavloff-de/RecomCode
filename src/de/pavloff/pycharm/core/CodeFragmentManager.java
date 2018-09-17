package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.worker.Worker;
import de.pavloff.pycharm.yaml.YamlLoader;

import javax.swing.table.TableModel;
import java.util.*;

public class CodeFragmentManager implements Worker {

    // TODO: make configurable
    private CodeFragmentLoader loader = new YamlLoader();

    private Map<String, Worker> workers = new HashMap<>();

    private List<CodeFragmentListener> codeFragmentListeners = new LinkedList<>();

    private TableModel selectedDataframe;
    private Map<String, CodeVariable> myVariables = new HashMap<>();

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    public void addCodeFragmentListener(CodeFragmentListener listener) {
        codeFragmentListeners.add(listener);
    }

    public void addWorker(Worker worker) {
        workers.put(worker.workerName(), worker);
    }

    CodeFragmentLoader getLoader() {
        return loader;
    }

    private void returnRecommendations() {
        CodeFragment.FragmentSorter sorter = new CodeFragment.FragmentSorter();

        LinkedHashSet<CodeFragment> recommendation;
        Iterator<CodeFragment> it;
        int rank;

        for (Worker worker : workers.values()) {
            recommendation = worker.getRecommendation();
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

    @Override
    public void onInput(String input) {
        for (Worker worker : workers.values()) {
            worker.onInput(input);
        }
        returnRecommendations();
    }

    @Override
    public void dataframeSelected(TableModel table) {
        for (Worker worker : workers.values()) {
            worker.dataframeSelected(table);
        }

        selectedDataframe = table;
        myVariables.put("dataframe", new CodeVariable.Builder()
                .setType("DataFrame").setName("df").build());

        returnRecommendations();
    }

    @Override
    public void cellSelected(int row, int column) {
        for (Worker worker : workers.values()) {
            worker.cellSelected(row, column);
        }

        myVariables.put("row_index", new CodeVariable.Builder()
                .setType("int").setName("row_index").setValue(String.valueOf(row)).build());
        myVariables.put("column_index", new CodeVariable.Builder()
                .setType("int").setName("column_index").setValue(String.valueOf(column)).build());

        if (selectedDataframe != null) {
            myVariables.put("column_name", new CodeVariable.Builder()
                    .setType("str").setName("column_name").setValue(selectedDataframe.getColumnName(column)).build());
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

        myVariables.put("row_index", new CodeVariable.Builder()
                .setType("int").setName("row_index").setValue(String.valueOf(row)).build());

        returnRecommendations();
    }

    @Override
    public void columnSelected(int column) {
        for (Worker worker : workers.values()) {
            worker.columnSelected(column);
        }

        myVariables.put("column_index", new CodeVariable.Builder()
                .setType("int").setName("column_index").setValue(String.valueOf(column)).build());

        if (selectedDataframe != null) {
            myVariables.put("column_name", new CodeVariable.Builder()
                    .setType("str").setName("column_name").setValue(selectedDataframe.getColumnName(column)).build());
        }

        returnRecommendations();
    }

    @Override
    public void codeFragmentSelected(CodeFragment fragment) {
        for (Worker worker : workers.values()) {
            worker.codeFragmentSelected(fragment);
        }
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
                myVariables.put(var.getType(), var);
            }
        }
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendation() {
        return null;
    }
}

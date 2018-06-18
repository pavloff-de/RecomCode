package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;

import javax.swing.table.TableModel;
import java.util.LinkedList;
import java.util.List;

public class SimpleWorker implements Worker {

    private CodeFragmentLoader loader;
    private List<CodeFragment> recommendations;
    private TableModel currentDataframe;

    public SimpleWorker(CodeFragmentLoader loader) {
        this.loader = loader;
    }

    public void onInput(String input) {
        // TODO: implement a delay for input
        recommendations = new LinkedList<>();
        String[] keywords = input.split(" ");

        for (CodeFragment fragment : loader.getCodeFragments(null)) {
            for (String keyword : keywords) {
                if (fragment.containsKeyword(keyword)) {
                    recommendations.add(fragment);
                }
            }
        }
    }

    @Override
    public String workerName() {
        return "Simple predictor";
    }

    @Override
    public String description() {
        return "Recommendations done based on single event";
    }

    @Override
    public void dataframeSelected(TableModel table) {
        currentDataframe = table;
        recommendationForDataframe();
    }

    @Override
    public void cellSelected(int row, int column) {
        // get important informations about cell, row, column
        recommendationForCells();
    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {
        // get important informations about cells, rows, columns
        recommendationForCells();
    }

    @Override
    public void rowSelected(int row) {
    // get important informations about row
        recommendationForRows();
    }

    @Override
    public void columnSelected(int column) {
        // get important informations about column
        recommendationForColumns();
    }

    @Override
    public List<CodeFragment> getRecommendation() {
        return recommendations;
    }

    @Override
    public void selectedCodeFragment(CodeFragment fragment) {

    }

    private void recommendationForDataframe() {

    }

    private void recommendationForRows() {

    }

    private void recommendationForColumns() {

    }

    private void recommendationForCells() {

    }
}

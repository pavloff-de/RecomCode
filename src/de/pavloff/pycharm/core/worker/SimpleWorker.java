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
    private String[] lastkeywords;
    private TableModel currentDataframe;

    public SimpleWorker(CodeFragmentLoader loader) {
        this.loader = loader;
    }

    public void onInput(String input) {
        // TODO: implement a delay for input
        lastkeywords = input.split(" ");
        searchForFragments(lastkeywords);
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
        // get important information about cells, rows, columns
        // TODO:
        //  less rows ? row by row
        //  less columns ? column by column
        //  ? cell by cell
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
        // TODO: ?
    }

    private void recommendationForDataframe() {
        searchForFragments("dataframe");
    }

    private void recommendationForRows() {
        searchForFragments("row");
    }

    private void recommendationForColumns() {
        searchForFragments("column");
    }

    private void recommendationForCells() {
        // TODO: ?
    }

    private void searchForFragments(String keyword) {
        searchForFragments(new String[] {keyword});
    }

    private void searchForFragments(String[] keywords) {
        recommendations = new LinkedList<>();

        for (CodeFragment fragment : loader.getCodeFragments(null)) {
            for (String keyword : keywords) {
                if (keyword.length() != 0 && fragment.containsKeyword(keyword)) {
                    recommendations.add(fragment);
                }
            }
        }
    }
}

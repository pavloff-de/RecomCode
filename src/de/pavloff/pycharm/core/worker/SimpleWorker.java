package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;

import javax.swing.table.TableModel;
import java.util.LinkedHashSet;
import java.util.List;

public class SimpleWorker implements Worker {

    private CodeFragmentLoader loader;
    private LinkedHashSet<CodeFragment> recommendations;
    private String[] lastkeywords;
    private TableModel currentDataframe;
    private int currentRow = -1;
    private int currentColumn = -1;

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
        currentRow = -1;
        currentColumn = -1;
        recommendationForDataframe();
    }

    @Override
    public void cellSelected(int row, int column) {
        // get important informations about cell, row, column
        currentRow = row;
        currentColumn = column;
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
        // get important information about row
        currentRow = row;
        currentColumn = -1;
        recommendationForRows();
    }

    @Override
    public void columnSelected(int column) {
        // get important informations about column
        currentRow = -1;
        currentColumn = column;
        recommendationForColumns();
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendation() {
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
        recommendations = new LinkedHashSet<>();

        for (CodeFragment fragment : loader.getCodeFragments(null)) {
            for (String keyword : keywords) {
                if (keyword.length() != 0 && fragment.containsKeyword(keyword)) {
                    recommendations.add(fragment);
                }
            }
        }
    }
}

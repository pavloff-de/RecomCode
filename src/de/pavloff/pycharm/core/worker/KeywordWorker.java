package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.*;

public class KeywordWorker implements Worker {

    private CodeFragmentLoader loader;
    private LinkedHashSet<CodeFragment> recommendations;
    private List<String> keywords;
    private List<String> inputs;
    private TableModel currentDataframe;

    public KeywordWorker(CodeFragmentLoader loader) {
        this.loader = loader;
        keywords = new LinkedList<>();
        inputs = new LinkedList<>();
    }

    @Override
    public String workerName() {
        return "Simple predictor";
    }

    @Override
    public String description() {
        return "Recommendations done based on single event";
    }

    private void addKeyword(String keyword) {
        final String kw = keyword.trim().replace("^[^A-Za-z0-9]+", "")
                .replace("[^A-Za-z0-9]+$", "").toLowerCase();
        if (kw.length() < 2) {
            return;
        }
        keywords.removeIf(k -> k.equals(kw));
        keywords.add(keyword);
    }

    public void onInput(String input) {
        inputs.clear();
        Collections.addAll(inputs, input.toLowerCase().split(" "));
        searchForFragments();
    }

    @Override
    public void dataframeSelected(String tableName, TableModel table) {
        currentDataframe = table;
        addKeyword("dataframe");
        searchForFragments();
    }

    @Override
    public void cellSelected(int row, int column) {
        // get important information about cell, row, column
        addKeyword("cell");
        addKeyword("row");
        addKeyword("column");
        searchForFragments();
    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {
        // get important information about cells, rows, columns
        // TODO:
        //  less rows ? row by row
        //  less columns ? column by column
        //  ? cell by cell
        addKeyword("cell");
        addKeyword("row");
        addKeyword("column");
        searchForFragments();
    }

    @Override
    public void rowSelected(int row) {
        // get important information about row
        addKeyword("row");
        searchForFragments();
    }

    @Override
    public void columnSelected(int column) {
        // get important information about column
        addKeyword("column");
        searchForFragments();
    }

    @Override
    public void codeFragmentSelected(CodeFragment fragment) {
        keywords.clear();
    }

    @Override
    public void sourceCode(String code) {

    }

    @Override
    public void codeVariables(Map<String, CodeVariable> variables) {

    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        return recommendations;
    }

    private void searchForFragments() {
        CodeFragment.FragmentSorter sorter = new CodeFragment.FragmentSorter();

        for (CodeFragment fragment : loader.getCodeFragments()) {
            int rating = rate(fragment);

            if (rating == 0) {
                continue;
            }

            sorter.add(fragment, rating);
        }

        recommendations = sorter.sortFragments();
    }

    private int rate(CodeFragment fragment) {
        int rating = 0;

        List<String> fragmentKeywords = fragment.getKeywords();
        if (fragmentKeywords == null || fragmentKeywords.size() == 0) {
            return rating;
        }

        String fragmentKeyword = String.join("", fragmentKeywords).toLowerCase();

        for (String keyword : keywords) {
            if (fragmentKeyword.contains(keyword)) {
                rating++;
            }
        }

        for (String keyword : inputs) {
            if (fragmentKeyword.contains(keyword)) {
                rating++;
            }
        }

        return rating;
    }
}

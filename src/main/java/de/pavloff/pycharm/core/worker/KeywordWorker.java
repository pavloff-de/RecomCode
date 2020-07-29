package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeVariable;
import de.pavloff.pycharm.plugin.YamlLoader;

import javax.swing.table.TableModel;
import java.util.*;

/** Worker using keywords to recommend code fragments
 * It converts user input and selections in a pandas dataframe to keywords to
 * search for the keywords and calculate most likely fragments
 */
public class KeywordWorker extends Worker {

    private Project openedProject;

    private LinkedHashSet<CodeFragment> recommendations;

    /**
     * contains the generated keywords
     */
    private List<String> keywords;

    /**
     * contains the user input
     */
    private List<String> inputs;

    @Override
    public void initialize(Project project) {
        openedProject = project;
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

    @Override
    protected void inputProcessing(String input) {
        inputs.clear();
        Collections.addAll(inputs, input.toLowerCase().split(" "));
        searchForFragments();
    }

    @Override
    protected void dataframeProcessing(String tableName, TableModel table) {
        addKeyword("dataframe");
        searchForFragments();
    }

    @Override
    protected void cellProcessing(int row, int column) {
        // get important information about cell, row, column
        addKeyword("cell");
        addKeyword("row");
        addKeyword("column");
        searchForFragments();
    }

    @Override
    protected void cellsProcessing(List<Pair<Integer, Integer>> cells) {
        if (cells == null || cells.size() == 0) {
            return;
        }

        if (cells.size() == 1) {
            cellProcessing(cells.get(0).first, cells.get(0).second);
            return;
        }

        Set<Integer> rows = new HashSet<>();
        Set<Integer> cols = new HashSet<>();

        for (Pair<Integer, Integer> cell : cells) {
            rows.add(cell.first);
            cols.add(cell.second);
        }

        int numRows = rows.size();
        int numCols = cols.size();

        addKeyword("cell");

        if (numRows == 1) {
            // many cells in one row
            addKeyword("row");
            addKeyword("columns");

        } else if (numRows > numCols) {
            if (numCols == 1) {
                // many cells in one column
                addKeyword("column");
            }
            // more rows as columns
            addKeyword("rows");
        } else {
            // more columns as rows
            addKeyword("columns");
        }

        searchForFragments();
    }

    @Override
    protected void rowProcessing(int row) {
        // get important information about row
        addKeyword("row");
        searchForFragments();
    }

    @Override
    protected void columnProcessing(int column) {
        // get important information about column
        addKeyword("column");
        searchForFragments();
    }

    @Override
    protected void sourcecodeProcessing(String code) {
    }

    @Override
    protected void variablesProcessing(Map<String, CodeVariable> variables) {
    }

    @Override
    protected void codeFragmentProcessing(CodeFragment fragment) {
        keywords.clear();
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        return recommendations;
    }

    private void addKeyword(String keyword) {
        final String kw = keyword.trim().replace("^[^A-Za-z0-9]+", "")
                .replace("[^A-Za-z0-9]+$", "").toLowerCase();
        if (kw.length() < 2) {
            return;
        }
        keywords.removeIf(k -> k.contains(kw));
        keywords.add(keyword);
    }

    /**
     * searches for fragments and rate them
     */
    private void searchForFragments() {
        CodeFragment.FragmentSorter sorter = new CodeFragment.FragmentSorter();
        CodeFragmentLoader loader = YamlLoader.getInstance(openedProject);

        for (CodeFragment fragment : loader.getCodeFragments()) {
            int rating = rate(fragment);

            if (rating == 0) {
                continue;
            }

            sorter.add(fragment, rating);
        }

        recommendations = sorter.getSortedFragments(5);
    }

    /**
     * rates a fragment on
     * - number of matches
     * - exact matches
     * - match the last keyword
     * - user input
     */
    private int rate(CodeFragment fragment) {
        int rating = 0;

        String fragmentKeyword = "";
        List<String> fragmentKeywords = fragment.getKeywords();
        if (fragmentKeywords != null) {
            fragmentKeyword = String.join("", fragmentKeywords).toLowerCase();
        }

        String fragmentTextkey = "";
        List<String> fragmentTextkeys = fragment.getTextkeys();
        if (fragmentTextkeys != null) {
            fragmentKeyword = String.join("", fragmentTextkeys).toLowerCase();
        }

        for (int i = 0; i < keywords.size(); i++) {
            String keyword = keywords.get(i);
            int rateIdx = i + 1;

            if (fragmentKeyword.contains(keyword)) {
                rating += rateIdx;

                if (rateIdx == keywords.size()) {
                    rating += 1; // more weight for last keys
                }
            }

            if (fragmentKeyword.equals(keyword)) {
                rating += 3; // more weight for exact keys

                if (rateIdx == keywords.size()) {
                    rating += 3; // more weight for last keys
                }
            }
        }

        // rank on user input more than on keywords
        for (String keyword : inputs) {
            if (fragmentKeyword.contains(keyword)) {
                rating += 5;
            }
            if (fragmentTextkey.contains(keyword)) {
                rating += 5;
            }
        }

        return rating;
    }
}

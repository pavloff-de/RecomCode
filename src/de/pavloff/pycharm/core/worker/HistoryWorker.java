package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.*;

public class HistoryWorker implements Worker {

    private CodeFragmentLoader loader;

    private List<CodeFragment> history;

    public HistoryWorker(CodeFragmentLoader loader) {
        this.loader = loader;
        history = new LinkedList<>();
    }

    @Override
    public String workerName() {
        return "History recommender";
    }

    @Override
    public String description() {
        return "Recommends code fragments from history";
    }

    @Override
    public void onInput(String input) {

    }

    @Override
    public void dataframeSelected(TableModel table) {

    }

    @Override
    public void cellSelected(int row, int column) {

    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {

    }

    @Override
    public void rowSelected(int row) {

    }

    @Override
    public void columnSelected(int column) {

    }

    @Override
    public void codeFragmentSelected(CodeFragment fragment) {
        history.removeIf(k -> k.equals(fragment));
        history.add(0, fragment);
    }

    @Override
    public void sourceCode(String code) {

    }

    @Override
    public void codeVariables(Map<String, CodeVariable> variables) {

    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendation() {
        LinkedHashSet<CodeFragment> lastFragments = new LinkedHashSet<>();

        if (history.size() != 0) {
            // find last 5 fragments
            ListIterator<CodeFragment> it = history.listIterator(Math.max(history.size() - 5, 1) - 1);
            while (it.hasNext()) {
                lastFragments.add(it.next());
            }
        }

        return lastFragments;
    }
}

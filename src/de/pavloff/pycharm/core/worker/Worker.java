package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;

import javax.swing.table.TableModel;
import java.util.LinkedHashSet;
import java.util.List;

public interface Worker {

    String workerName();

    String description();

    void onInput(String input);

    void dataframeSelected(TableModel table);

    void cellSelected(int row, int column);

    void cellsSelected(List<Pair<Integer, Integer>> cells);

    void rowSelected(int row);

    void columnSelected(int column);

    void codeFragmentSelected(CodeFragment fragment);

    void sourceCode(String code);

    LinkedHashSet<CodeFragment> getRecommendation();
}

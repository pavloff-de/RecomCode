package de.pavloff.pycharm.plugin.serverstub;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeVariable;

import javax.swing.table.TableModel;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** ServerStub is the object handing all interactions with the "engine".
 * It replaces previous usage of CodeFragmentManager.
 */
public interface ServerStub {

    // can be used for initializing of service
    void initialize(Project project);

    // returns recommendations
    LinkedHashSet<CodeFragment> getRecommendations();

    // ========= callbacks from varviewer panel ========= //

    // dataframe selected
    void onDataframe(String tableName, TableModel table);

    // one cell of a dataframe selected
    void onCell(int row, int column);

    // many cells of a dataframe selected
    void onCells(List<Pair<Integer, Integer>> cells);

    // row of a dataframe selected
    void onRow(int row);

    // column of a dataframe selected
    void onColumn(int column);

    // variables from source code
    void onVariables(Map<String, CodeVariable> variables);

    // source code
    void onSourcecode(String code);

    // ========= callbacks from recommender panel ========= //

    // user input
    void onInput(String input);

    // code fragment selected
    void onCodeFragment(CodeFragment fragment);
}

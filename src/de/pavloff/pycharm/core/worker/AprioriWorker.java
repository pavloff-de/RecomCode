package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.mrapp.apriori.*;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableModel;
import java.util.*;

public class AprioriWorker implements Worker {

    private CodeFragmentLoader loader;
    private LinkedHashSet<CodeFragment> recommendations;
    private int maxRecommendations = 5;
    private LinkedList<MyItem> items;
    private ArrayList<MyItem> newItems;
    private LinkedList<String> keywords;

    private Apriori<MyItem> apriori;

    public AprioriWorker(CodeFragmentLoader loader) {
        this.loader = loader;
        apriori = new Apriori.Builder<MyItem>(0.01).generateRules(0.02).ruleCount(100).create();
        items = new LinkedList<>();
        keywords = new LinkedList<>();
    }

    @Override
    public String workerName() {
        return "Apriori recommender";
    }

    @Override
    public String description() {
        return "Recommends code fragments using association rule learning";
    }

    @Override
    public void onInput(String input) {
        if (input.length() == 0) {
            return;
        }
        items.add(new MyItem("Input_" + input));
        keywords.add(input);
        searchForFragments();
    }

    @Override
    public void dataframeSelected(TableModel table) {
        items.add(new MyItem("DataFrame_" + table.toString()));
        keywords.add("dataframe");
        searchForFragments();
    }

    @Override
    public void cellSelected(int row, int column) {
        items.add(new MyItem("Cell_" + String.valueOf(row) + "_" + String.valueOf(column)));
        keywords.add("cell");
        keywords.add("row");
        keywords.add("column");
        searchForFragments();
    }

    @Override
    public void cellsSelected(List<Pair<Integer, Integer>> cells) {
        int s = cells.size();
        if (s == 0) {
            return;
        }

        Pair first = cells.get(0);
        if (s == 1) {
            cellSelected((Integer) first.first, (Integer) first.second);
            return;
        }

        Pair last = cells.get(s - 1);
        items.add(new MyItem("Cell_" + String.valueOf(first.first) + "_" + String.valueOf(first.second)));
        items.add(new MyItem("Cell_" + String.valueOf(last.first) + "_" + String.valueOf(last.second)));
        keywords.add("cell");
        keywords.add("row");
        keywords.add("column");
        searchForFragments();
    }

    @Override
    public void rowSelected(int row) {
        items.add(new MyItem("Row_" + String.valueOf(row)));
        keywords.add("row");
        searchForFragments();
    }

    @Override
    public void columnSelected(int column) {
        items.add(new MyItem("Column_" + String.valueOf(column)));
        keywords.add("column");
        searchForFragments();
    }

    @Override
    public LinkedHashSet<CodeFragment> getRecommendation() {
        return recommendations;
    }

    @Override
    public void selectedCodeFragment(CodeFragment fragment) {
        items.add(new MyItem(fragment));
        keywords.clear();
    }

    private Iterable<Transaction<MyItem>> getLastTransactions() {
        // split items into transactions
        LinkedList<Transaction<MyItem>> transactions = new LinkedList<>();

        MyTransaction fullTransaction = new MyTransaction();

        for (MyItem i : items) {
            if (i.isCodeFragment) {
                if (fullTransaction.size() != 0) {
                    for (int k = 1; k < 6; k++) {
                        for (MyTransaction t : subTransactions(fullTransaction.getList(), k)) {
                            t.add(i);
                            transactions.add(t);
                        }
                    }
                    fullTransaction = new MyTransaction();
                }
            } else {
                fullTransaction.add(i);
            }
        }

        newItems = fullTransaction.getList();

        return transactions;
    }

    private List<MyTransaction> subTransactions(ArrayList<MyItem> input, int k) {
        List<MyTransaction> subsets = new LinkedList<>();

        int[] s = new int[k];

        if (k <= input.size()) {
            for (int i = 0; (s[i] = i) < k - 1; i++);
            subsets.add(getSubset(input, s));

            for(;;) {
                int i;
                // find position of item that can be incremented
                for (i = k - 1; i >= 0 && s[i] == input.size() - k + i; i--);
                if (i < 0) {
                    break;
                }
                s[i]++;                    // increment this item
                for (++i; i < k; i++) {    // fill up remaining items
                    s[i] = s[i - 1] + 1;
                }
                subsets.add(getSubset(input, s));
            }
        }

        return subsets;
    }

    // generate actual subset by index sequence
    private MyTransaction getSubset(ArrayList<MyItem> input, int[] subset) {
        MyTransaction result = new MyTransaction();
        for (int aSubset : subset) {
            result.add(input.get(aSubset));
        }
        return result;
    }

    private void searchForFragments() {
        recommendations = new LinkedHashSet<>();

        Output<MyItem> output = apriori.execute(getLastTransactions());
        RuleSet<MyItem> ruleSet = output.getRuleSet();

        if (ruleSet != null && newItems.size() != 0) {
            for (AssociationRule<MyItem> rule : ruleSet) {
                if (rule.covers(newItems.get(newItems.size() - 1))) {
                    ItemSet<MyItem> head = rule.getHead();

                    for (MyItem item : head) {
                        if (item.isCodeFragment && recommendations.size() < maxRecommendations) {
                            recommendations.add(item.getCodeFragment());
                        }
                    }
                }
            }
        } else {
            searchForFragmentsIfNoTransactions();
            return;
        }
        if (recommendations.size() == maxRecommendations) {
            return;
        }

        searchForFragmentsIfNoTransactions();
    }

    private void searchForFragmentsIfNoTransactions() {
        if (keywords.size() == 0) {
            return;
        }

        for (CodeFragment fragment : loader.getCodeFragments(null)) {
            String keyword = keywords.getLast();
            if (recommendations.size() < maxRecommendations && keyword.length() != 0 && fragment.containsKeyword(keyword)) {
                recommendations.add(fragment);
            }
        }
    }

    private class MyTransaction implements Transaction<MyItem> {

        private LinkedList<MyItem> list;

        MyTransaction() {
            list = new LinkedList<>();
        }

        public Boolean add(MyItem item) {
            return list.add(item);
        }

        public int size() {
            return list.size();
        }

        @NotNull
        @Override
        public Iterator<MyItem> iterator() {
            return list.iterator();
        }

        public ArrayList<MyItem> getList() {
            ArrayList<MyItem> a = new ArrayList<>(list.size());
            a.addAll(list);
            return a;
        }

        @Override
        public String toString() {
            return list.toString();
        }
    }

    private class MyItem implements Item {

        private CodeFragment codeFragment;
        private Boolean isCodeFragment;
        private String itemName;

        MyItem(String selection) {
            this.itemName = selection;
            this.isCodeFragment = false;
        }

        MyItem(CodeFragment codeFragment) {
            this.codeFragment = codeFragment;
            this.itemName = codeFragment.getRecID();
            this.isCodeFragment = true;
        }

        CodeFragment getCodeFragment() {
            return codeFragment;
        }

        @Override
        public int compareTo(@NotNull Item o) {
            if (this.equals(o)) {
                return 0;
            }

            return itemName.compareTo(((MyItem) o).itemName);
        }

        @Override
        public final int hashCode() {
            return itemName.hashCode();
        }

        @Override
        public final boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return itemName.equals(((MyItem) obj).itemName);
        }

        @Override
        public String toString() {
            return this.itemName;
        }
    }
}

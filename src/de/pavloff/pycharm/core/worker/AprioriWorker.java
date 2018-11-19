package de.pavloff.pycharm.core.worker;

import com.intellij.openapi.util.Pair;
import de.mrapp.apriori.*;
import de.mrapp.apriori.metrics.Confidence;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeVariable;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableModel;
import java.util.*;

/** Worker using apriori algorithm to recommend code fragments
 * It uses user input and selection in a pandas dataframe to calculate most likely
 * fragments
 */
public class AprioriWorker extends Worker {

    private LinkedHashSet<CodeFragment> recommendations;

    private LinkedList<MyItem> items;

    private ArrayList<MyItem> newItems;

    private Apriori<MyItem> apriori;

    @Override
    public void initialize() {
        apriori = new Apriori.Builder<MyItem>(0.01).generateRules(0.02).ruleCount(100).create();
        items = new LinkedList<>();
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
    protected void inputProcessing(String input) {
        if (input.length() == 0) {
            return;
        }
        items.add(new MyItem("Input_" + input));
        searchForFragments();
    }

    @Override
    protected void dataframeProcessing(String tableName, TableModel table) {
        items.add(new MyItem("DataFrame_" + table.toString()));
        searchForFragments();
    }

    @Override
    protected void cellProcessing(int row, int column) {
        items.add(new MyItem("Cell_" + String.valueOf(row) + "_" + String.valueOf(column)));
        searchForFragments();
    }

    @Override
    protected void cellsprocessing(List<Pair<Integer, Integer>> cells) {
        int s = cells.size();
        if (s == 0) {
            return;
        }

        Pair first = cells.get(0);
        if (s == 1) {
            onCell((Integer) first.first, (Integer) first.second);
            return;
        }

        Pair last = cells.get(s - 1);
        items.add(new MyItem("Cell_" + String.valueOf(first.first) + "_" + String.valueOf(first.second)));
        items.add(new MyItem("Cell_" + String.valueOf(last.first) + "_" + String.valueOf(last.second)));
        searchForFragments();
    }

    @Override
    protected void rowProcessing(int row) {
        items.add(new MyItem("Row_" + String.valueOf(row)));
        searchForFragments();
    }

    @Override
    protected void columnProcessing(int column) {
        items.add(new MyItem("Column_" + String.valueOf(column)));
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
        items.add(new MyItem(fragment));
    }


    @Override
    public LinkedHashSet<CodeFragment> getRecommendations() {
        return recommendations;
    }

    /**
     * returns user input split in transactions
     */
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

    /**
     * generates sub transactions of max size k
     */
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

    /**
     * generates actual subset by index sequence
     */
    private MyTransaction getSubset(ArrayList<MyItem> input, int[] subset) {
        MyTransaction result = new MyTransaction();
        for (int aSubset : subset) {
            result.add(input.get(aSubset));
        }
        return result;
    }

    /**
     * runs the apriori algorithm
     */
    private void searchForFragments() {
        recommendations = new LinkedHashSet<>();

        Output<MyItem> output = apriori.execute(getLastTransactions());
        RuleSet<MyItem> ruleSet = output.getRuleSet();

        if (ruleSet == null || newItems.size() == 0) {
            return;
        }

        RuleSet<MyItem> filteredRuleSet = ruleSet.filter(
                // TODO: adapt minPerformance to find good results
                Filter.forAssociationRules().byOperator(new RuleFilter(), 0.5));
        RuleSet<MyItem> sortedRuleSet = filteredRuleSet.sort(
                Sorting.forAssociationRules().withOrder(Sorting.Order.DESCENDING).byOperator(new Confidence()));

        for (AssociationRule<MyItem> rule : sortedRuleSet) {
            if (rule.covers(newItems.get(newItems.size() - 1))) {
                ItemSet<MyItem> head = rule.getHead();

                for (MyItem item : head) {
                    recommendations.add(item.getCodeFragment());
                }
            }
        }
    }

    /**
     * filter used to filter out the transactions with code fragment in a head
     */
    private class RuleFilter implements Operator {

        @Override
        public double evaluate(@NotNull AssociationRule rule) {
            ItemSet target = rule.getHead();
            if (target.size() != 1) {
                return 0;
            }

            MyItem item = (MyItem) target.first();
            if (!item.isCodeFragment) {
                return 0;
            }

            return 1;
        }
    }

    /**
     * class containing the user transaction / list of actions
     * the last action should be the selected code fragment
     */
    private class MyTransaction implements Transaction<MyItem> {

        private LinkedList<MyItem> list;

        MyTransaction() {
            list = new LinkedList<>();
        }

        void add(MyItem item) {
            list.add(item);
        }

        int size() {
            return list.size();
        }

        @NotNull
        @Override
        public Iterator<MyItem> iterator() {
            return list.iterator();
        }

        public ArrayList<MyItem> getList() {
            int listSize = list.size();
            List<MyItem> subList = list;

            if (listSize > 5) {
                // take just last 5 items
                subList = list.subList(listSize - 6, listSize - 1);
            }

            ArrayList<MyItem> a = new ArrayList<>(subList.size());
            a.addAll(subList);
            return a;
        }

        @Override
        public String toString() {
            return list.toString();
        }
    }

    /**
     * user action
     * can be the selection of a code fragment or some user input
     */
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

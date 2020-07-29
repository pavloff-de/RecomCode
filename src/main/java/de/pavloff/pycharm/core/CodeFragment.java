package de.pavloff.pycharm.core;

import com.intellij.openapi.util.Pair;

import java.util.*;


/** Main class for recommendations created by {@link CodeFragmentLoader}
 * It contains the code fragment, params used in the code and other additional fields
 */
public class CodeFragment {

    /**
     * unique identifier of the fragment
     */
    private final String recID;

    /**
     * name of the group the fragment belongs to
     */
    private final String group;

    /**
     * list of the subgroups the fragment belongs to
     */
    private final ArrayList<String> subgroup;

    /**
     * the parent's recID of the fragment
     */
    private final String parent;

    /**
     * list of related fragments
     */
    private final ArrayList<String> related;

    /**
     * description of the fragment displayed in a recommendation
     * can contain synonymous words separated by |
     */
    private final ArrayList<String> textkeys;

    /**
     * description of the fragment displayed in a recommendation
     * dynamically build, can contain variable names
     */
    private final String textkey;

    /**
     * keyword used to search for the fragment
     */
    private final ArrayList<String> keywords;

    /**
     * description or url of the source
     */
    private final String sources;

    /**
     * description or url of the documentation
     */
    private final String documentation;

    /**
     * the code fragment used to create a live template
     */
    private final String code;

    /**
     * list of param names used in code fragment
     */
    private final String[] paramsList;

    /**
     * list of params in code fragment
     */
    private final Map<String, CodeParam> defaultParams;

    // creates an object using builder pattern
    private CodeFragment(Builder builder) {
        this.recID = builder.recID;
        this.group = builder.group;
        this.subgroup = builder.subgroup;
        this.parent = builder.parent;
        this.related = builder.related;
        this.textkeys = builder.textkeys;
        this.textkey = builder.textkey;
        this.keywords = builder.keywords;
        this.sources = builder.sources;
        this.documentation = builder.documentation;
        this.code = builder.code;
        this.paramsList = builder.paramsList;
        this.defaultParams = builder.defaultParams;
    }

    public String getRecID() {
        return recID;
    }

    public String getGroup() {
        return group;
    }

    public ArrayList<String> getSubgroup() {
        return subgroup;
    }

    public ArrayList<String> getTextkeys() {
        return textkeys;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public String getCode() {
        return code;
    }

    public String[] getParamsList() {
        return paramsList;
    }

    public Map<String, CodeParam> getDefaultParams() {
        return defaultParams;
    }

    /**
     * replaces code params with variables from context
     * creates many fragments if some variable is not unique
     */
    public List<CodeFragment> getWithVariables(Map<String, CodeVariable> variables) {
        List<CodeFragment> withVariables = new ArrayList<>();
        Map<String, CodeParam> newParams = new HashMap<>();

        String newTextKey = "";
        if (textkeys.size() != 0) {
            newTextKey = textkeys.get(0);
        }

        for (Map.Entry<String, CodeVariable> varEntry : variables.entrySet()) {
            String parName = varEntry.getKey();

            if (defaultParams.containsKey(parName)) {
                CodeVariable lastVar = varEntry.getValue();
                newParams.put(parName, new CodeParam.Builder().setRecId(recID).setGroup(group)
                        .setExpr("").setName(lastVar.getName()).setVars(lastVar.getValue()).build());

                if (newTextKey.contains(parName)) {
                    String varName = String.format("%s \"%s\"", parName,
                            lastVar.getValue());
                    newTextKey = newTextKey.replace(parName, varName);
                }
            }
        }

        if (newParams.size() != 0) {
            Builder builder = new Builder().setRecId(recID).setGroup(group)
                    .setTextkey(newTextKey).setTextkeys(textkeys)
                    .setKeywords(keywords).setSources(sources)
                    .setCode(code).setParamsList(paramsList);

            Map<String, CodeParam> updatedParams = new HashMap<>(defaultParams);
            updatedParams.putAll(newParams);
            builder.setDefaultParams(updatedParams);

            withVariables.add(builder.build());
        }

        return withVariables;
    }

    /**
     * returns a text to display it in a recommendation
     */
    public String getCleanTextkey() {
        String cleanTextkey;
        if (textkey != null) {
            cleanTextkey = textkey;
        } else if (textkeys == null || textkeys.size() == 0) {
            return null;
        } else {
            cleanTextkey = textkeys.get(0);
        }

        String splitter = " ";
        String[] split = cleanTextkey.split(splitter);

        for (int i = 0; i < split.length; i++) {
            // take the first one
            // TODO: take a word depending on user input
            split[i] = split[i].split("\\|")[0];
        }

        return String.join(splitter, split);
    }

    @Override
    public final int hashCode() {
        return (recID + getCleanTextkey()).hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return this.hashCode() == obj.hashCode();
    }

    /**
     * Main class to create an object of {@link CodeFragment} using builder pattern
     * It contains set methods returning always a Builder instance
     */
    public static class Builder {

        private String recID;
        private String group;
        private ArrayList<String> subgroup;
        private String parent;
        private ArrayList<String> related;
        private ArrayList<String> textkeys;
        private String textkey;
        private ArrayList<String> keywords;
        private String sources;
        private String documentation;
        private String code;
        private String[] paramsList;
        private Map<String, CodeParam> defaultParams;

        public Builder setRecId(String recID) {
            this.recID = recID;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setSubgroup(ArrayList<String> subgroup) {
            this.subgroup = subgroup;
            return this;
        }

        public Builder setParent(String parent) {
            this.parent = parent;
            return this;
        }

        public Builder setRelated(ArrayList<String> related) {
            this.related = related;
            return this;
        }

        public Builder setTextkeys(ArrayList<String> textkeys) {
            this.textkeys = textkeys;
            return this;
        }

        public Builder setTextkey(String textkey) {
            this.textkey = textkey;
            return this;
        }

        public Builder setKeywords(ArrayList<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder setSources(String sources) {
            this.sources = sources;
            return this;
        }

        public Builder setDocumentation(String documentation) {
            this.documentation = documentation;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setParamsList(String[] paramsList) {
            this.paramsList = paramsList;
            return this;
        }

        public Builder setDefaultParams(Map<String, CodeParam> parameterValues) {
            this.defaultParams = parameterValues;
            return this;
        }

        /**
         * returns a new object of {@link CodeFragment} with set fields
         */
        public CodeFragment build() {
            return new CodeFragment(this);
        }
    }

    /**
     * The sorter of {@link CodeFragment}
     * It give a possibility to rank a code fragment to sort on it.
     */
    public static class FragmentSorter {
        Map<CodeFragment, Double> ratings;

        public FragmentSorter() {
            ratings = new HashMap<>();
        }

        public void add(CodeFragment fragment) {
            add(fragment, 1.0);
        }

        public void add(CodeFragment fragment, double rating) {
            if (!ratings.containsKey(fragment)) {
                ratings.put(fragment, rating);
            } else {
                ratings.put(fragment, ratings.get(fragment) + rating);
            }
        }

        public void remove(CodeFragment fragment) {
            ratings.remove(fragment);
        }

        /** returns an iterator of sorted list
         */
        private ListIterator sortFragments() {
            List<Map.Entry<CodeFragment, Double>> ratedFragments =
                    new ArrayList<>(ratings.entrySet());
            ratedFragments.sort(Map.Entry.comparingByValue());
            return ratedFragments.listIterator(ratedFragments.size());
        }

        /**
         * returns a set of sorted fragments
         */
        public LinkedHashSet<CodeFragment> getSortedFragments() {
            ListIterator it = sortFragments();
            LinkedHashSet<CodeFragment> sortedFragments = new LinkedHashSet<>();

            while(it.hasPrevious()) {
                Map.Entry bestRec = (Map.Entry) it.previous();
                sortedFragments.add((CodeFragment) bestRec.getKey());
            }

            return sortedFragments;
        }

        /**
         * returns a set of sorted fragments and its rating
         */
        public LinkedHashSet<Pair<Double, CodeFragment>> getSortedFragmentsWithRating() {
            ListIterator it = sortFragments();
            LinkedHashSet<Pair<Double, CodeFragment>> sortedFragmentsWithRating =
                    new LinkedHashSet<>();

            while(it.hasPrevious()) {
                Map.Entry bestRec = (Map.Entry) it.previous();
                sortedFragmentsWithRating.add(new Pair<>((Double) bestRec.getValue(),
                        (CodeFragment) bestRec.getKey()));
            }

            return sortedFragmentsWithRating;
        }
    }
}

package de.pavloff.pycharm.core;

import java.util.*;

public class CodeFragment {

    private final String recID;
    private final String group;
    private final String parent;
    private final ArrayList<String> related;
    private final ArrayList<String> textkeys;
    private final ArrayList<String> keywords;
    private final String sources;
    private final String documentation;
    private final String code;
    private final String[] paramsList;
    private final Map<String, CodeParam> defaultParams;

    private CodeFragment(Builder builder) {
        this.recID = builder.recID;
        this.group = builder.group;
        this.parent = builder.parent;
        this.related = builder.related;
        this.textkeys = builder.textkeys;
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

    public List<CodeFragment> getWithVariables(Map<String, List<CodeVariable>> variables) {
        List<CodeFragment> withVariables = new ArrayList<>();
        Map<String, CodeParam> newParams = new HashMap<>();

        String newTextKey = "";
        if (textkeys.size() != 0) {
            newTextKey = textkeys.get(0);
        }

        for (Map.Entry<String, List<CodeVariable>> varEntry : variables.entrySet()) {
            String parName = varEntry.getKey();

            if (defaultParams.containsKey(parName)) {
                List<CodeVariable> vars = varEntry.getValue();
                int vs = vars.size();

                if (vs > 0) {
                    // using just the last one
                    // TODO: if code contains many params of same variables
                    //       (e.g. dataframe, dataframe2) take next var and
                    //       make many combinations
                    CodeVariable lastVar = vars.get(vars.size() - 1);
                    newParams.put(parName, new CodeParam.Builder().setRecId(recID).setGroup(group)
                            .setExpr("").setName(lastVar.getName()).setVars(lastVar.getValue()).build());

                    if (newTextKey.contains(parName)) {
                        String varName = String.format("%s \"%s\"", parName,
                                lastVar.getValue());
                        newTextKey = newTextKey.replace(parName, varName);
                    }

                }
            }
        }

        if (newParams.size() != 0) {
            Builder builder = new Builder().setRecId(recID).setGroup(group)
                    .setKeywords(keywords).setSources(sources).setCode(code).setParamsList(paramsList);

            Map<String, CodeParam> updatedParams = new HashMap<>(defaultParams);
            updatedParams.putAll(newParams);
            builder.setDefaultParams(updatedParams);

            //TODO: replace text with varNames
            ArrayList<String> newTextKeys = new ArrayList<>();
            newTextKeys.add(newTextKey);
            builder.setTextkeys(newTextKeys);

            withVariables.add(builder.build());
        }

        return withVariables;
    }

    public String[] getCleanTextkeys() {
        if (textkeys == null || textkeys.size() == 0) {
            return null;
        }

        List<String> cleanTextkeys = new ArrayList<>();
        String splitter = " ";

        for (String tk : textkeys) {
            String[] split = tk.split(splitter);

            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].split("\\|")[0];
            }

            cleanTextkeys.add(String.join(splitter, split));
        }

        return cleanTextkeys.toArray(new String[0]);
    }

    public static class Builder {

        private String recID;
        private String group;
        private String parent;
        private ArrayList<String> related;
        private ArrayList<String> textkeys;
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

        public CodeFragment build() {
            return new CodeFragment(this);
        }
    }

    public static class FragmentSorter {
        Map<CodeFragment, Integer> ratings;

        public FragmentSorter() {
            ratings = new HashMap<>();
        }

        public void add(CodeFragment fragment) {
            add(fragment, 1);
        }

        public void add(CodeFragment fragment, int rating) {
            if (!ratings.containsKey(fragment)) {
                ratings.put(fragment, rating);
            } else {
                ratings.put(fragment, ratings.get(fragment) + rating);
            }
        }

        public LinkedHashSet<CodeFragment> sortFragments() {
            List<Map.Entry<CodeFragment, Integer>> ratedFragments = new ArrayList<>(ratings.entrySet());
            LinkedHashSet<CodeFragment> sortedFragments = new LinkedHashSet<>();
            ratedFragments.sort(Map.Entry.comparingByValue());
            ListIterator it = ratedFragments.listIterator(ratedFragments.size());

            while(it.hasPrevious()) {
                Map.Entry bestRec = (Map.Entry) it.previous();
                sortedFragments.add((CodeFragment) bestRec.getKey());
            }

            return sortedFragments;
        }
    }
}

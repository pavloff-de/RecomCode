package de.pavloff.pycharm.core;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeFragment {

    private final String recID;
    private final String group;
    private final String parent;
    private final ArrayList<String> related;
    private final ArrayList<String> textkey;
    private final ArrayList<String> keywords;
    private final String sources;
    private final String documentation;
    private final String code;
    private final Map<String, CodeParam> parameters;

    private CodeFragment(Builder builder) {
        this.recID = builder.recID;
        this.group = builder.group;
        this.parent = builder.parent;
        this.related = builder.related;
        this.textkey = builder.textkey;
        this.keywords = builder.keywords;
        this.sources = builder.sources;
        this.documentation = builder.documentation;
        this.code = builder.code;
        this.parameters = addCodeParams(builder.parameters);
    }

    private Map<String, CodeParam> addCodeParams(Map<String, CodeParam> codeParams) {
        Map<String, CodeParam> fragmentParams = new HashMap<>();

        for (String varName : searchCodeForVariables()) {
            if (codeParams.containsKey(varName)) {
                fragmentParams.put(varName, codeParams.get(varName));
            }
        }

        return fragmentParams;
    }

    public Set<String> searchCodeForVariables() {
        Set<String> visitedVariables = new HashSet<>();
        Matcher m = Pattern.compile("\\$(.*?)\\$").matcher(getCode());

        while (m.find()) {
            visitedVariables.add(m.group(1));
        }

        return visitedVariables;
    }

    public String getRecID() {
        return recID;
    }

    public String getGroup() {
        return group;
    }

    public String getCode() {
        return code;
    }

    public Map<String, CodeParam> getParameters() {
        return parameters;
    }

    public List<CodeFragment> getWithVariables(Map<String, CodeVariable> variables) {
        List<CodeFragment> withVariables = new ArrayList<>();
        Map<String, CodeParam> newParams = new HashMap<>();

        String newTextKey = "";
        if (textkey.size() != 0) {
            newTextKey = textkey.get(0);
        }

        for (Map.Entry<String, CodeVariable> varEntry : variables.entrySet()) {
            String parName = varEntry.getKey();

            if (parameters.containsKey(parName)) {
                CodeVariable var = varEntry.getValue();
                newParams.put(parName, new CodeParam.Builder().setRecId(recID).setGroup(group)
                        .setExpr("").setName(var.getType()).setVars(var.getName()).build());

                if (newTextKey.contains(parName)) {
                    newTextKey = newTextKey.replace(parName, var.getName());
                }
            }
        }

        if (newParams.size() != 0) {
            Builder builder = new Builder().setRecId(recID).setGroup(group)
                    .setKeywords(keywords).setSources(sources).setCode(code);

            Map<String, CodeParam> updatedParams = new HashMap<>(parameters);
            updatedParams.putAll(newParams);
            builder.setParameters(updatedParams);

            //TODO: replace text with varNames
            ArrayList<String> newTextKeys = new ArrayList<>();
            newTextKeys.add(newTextKey);
            builder.setTextkey(newTextKeys);

            withVariables.add(builder.build());
        }

        return withVariables;
    }

    private Boolean containsKeyword(String keyword) {
        if (keyword.length() == 0) {
            return false;
        }
        if (this.keywords == null) {
            return false;
        }
        return String.join("", keywords).toLowerCase().contains(keyword.toLowerCase());
    }

    public int containsKeywords(List<String> keywords) {
        if (this.keywords == null || keywords == null || keywords.size() == 0) {
            return 0;
        }

        int numMatches = 0;
        for (String keyword : keywords) {
            if (containsKeyword(keyword)) {
                numMatches++;
            }
        }
        return numMatches;
    }

    public String getCleanTextkey() {
        if (this.textkey == null) {
            return "";
        }
        if (this.textkey.size() == 0) {
            return "";
        }
        String[] split = this.textkey.get(0).split(" ");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].split("\\|")[0];
        }

        return String.join(" ", split);
    }

    public static class Builder {

        private String recID;
        private String group;
        private String parent;
        private ArrayList<String> related;
        private ArrayList<String> textkey;
        private ArrayList<String> keywords;
        private String sources;
        private String documentation;
        private String code;
        private Map<String, CodeParam> parameters;

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

        public Builder setTextkey(ArrayList<String> textkey) {
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

        public Builder setParameters(Map<String, CodeParam> parameterValues) {
            this.parameters = parameterValues;
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

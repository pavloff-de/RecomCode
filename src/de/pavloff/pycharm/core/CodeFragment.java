package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;

import java.util.*;

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
        this.parameters = builder.parameters;
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

    public Map<String, CodeParam> getGlobalParameters(Project openedProject) {
        Map<String, CodeParam> params = new HashMap<>();

        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        ArrayList<CodeParam> vars = recommender.getLoader().getCodeParams(null);

        for (CodeParam var : vars) {
            params.put(var.getName(), var);
        }

        return params;
    }

    public Map<String, CodeParam> getParameters() {
        return parameters;
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
        Iterator<String> it = keywords.iterator();
        while (it.hasNext()) {
            if (containsKeyword(it.next())) {
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

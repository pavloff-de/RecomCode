package de.pavloff.pycharm.core;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import de.pavloff.pycharm.core.macros.PyVariableMacro;

import java.util.ArrayList;
import java.util.Map;

public class CodeFragment {

    private final String recID;
    private final String group;
    private final String parent;
    private final ArrayList<String> related;
    private final ArrayList<String> textkey;
    private final ArrayList<String> keywords;
    private final String commentTemplate;
    private final String sources;
    private final String documentation;
    private final String code;
    private final Map<String,String[]> parameters;

    private CodeFragment(Builder builder) {
        this.recID = builder.recID;
        this.group = builder.group;
        this.parent = builder.parent;
        this.related = builder.related;
        this.textkey = builder.textkey;
        this.keywords = builder.keywords;
        this.commentTemplate = builder.commentTemplate;
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
    
    public Template getTemplate(TemplateManager templateManager) {
        Template t = templateManager.createTemplate(getRecID(), getGroup(), code);
        t.setToReformat(false);
        t.setToIndent(false);
        for (String paramName: parameters.keySet()) {
            String[] p = parameters.get(paramName);
            if (p[1].length() != 0) {
                t.addVariable(paramName, p[1], p[0], true);
            } else {
                MacroCallNode macro = new MacroCallNode(new PyVariableMacro(p[0].split("\\|")));
                t.addVariable(paramName, macro, true);
            }

        }
        return t;
    }

    public Boolean containsKeyword(String keyword) {
        if (keyword.length() == 0) {
            return false;
        }
        if (this.keywords == null) {
            return false;
        }
        return String.join("", keywords).toLowerCase().contains(keyword.toLowerCase());
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
        private String commentTemplate;
        private String sources;
        private String documentation;
        private String code;
        private Map<String,String[]> parameters;

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

        public Builder setCommentTemplate(String commentTemplate) {
            this.commentTemplate = commentTemplate;
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

        public Builder setParameters(Map<String,String[]> parameterValues) {
            this.parameters = parameterValues;
            return this;
        }

        public CodeFragment build() {
            return new CodeFragment(this);
        }
    }
}

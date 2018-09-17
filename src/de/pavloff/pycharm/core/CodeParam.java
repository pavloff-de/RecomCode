package de.pavloff.pycharm.core;

public class CodeParam {

    private final String recID;
    private final String group;
    private final String name;
    private final String type;
    private final String vars;
    private final String expr;

    public String getRecID() {
        return recID;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVars() {
        return vars;
    }

    public Boolean hasVars() {
        return !vars.equals("");
    }

    public String getExpr() {
        return expr;
    }

    public Boolean hasExpression() {
        return !expr.equals("");
    }

    private CodeParam(Builder builder) {
        this.recID = builder.recID;
        this.group = builder.group;
        this.name = builder.name;
        this.type = builder.type;
        this.vars = builder.vars;
        this.expr = builder.expr;
    }

    public static class Builder {

        private String recID;
        private String group;
        private String name;
        private String type;
        private String vars;
        private String expr;

        public Builder setRecId(String recID) {
            this.recID = recID;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setVars(String vars) {
            this.vars = vars;
            return this;
        }

        public Builder setExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public CodeParam build() {
            return new CodeParam(this);
        }
    }
}

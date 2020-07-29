package de.pavloff.pycharm.core;

/** Main class for params used in {@link CodeFragment}
 * It contains the name, var examples, var expression and other additional fields
 * It can be replaced with {@link CodeVariable}
 */
public class CodeParam {

    /**
     * unique identifier of the param
     */
    private final String recID;

    /**
     * name of the group the param belongs to
     */
    private final String group;

    /**
     * name of the param used in a code of {@link CodeFragment}
     * used for replacing with a {@link CodeVariable}
     */
    private final String name;

    /**
     * type of the param
     */
    private final String type;

    /**
     * possible values which can be used for this param
     * @see de.pavloff.pycharm.plugin.macros.PyVariableMacro
     */
    private final String vars;

    /**
     * expression used to search for variables in a context
     * @see de.pavloff.pycharm.plugin.macros.PyUniqueIterableVariableMacro
     */
    private final String expr;

    // creates an object using builder pattern
    private CodeParam(Builder builder) {
        this.recID = builder.recID;
        this.group = builder.group;
        this.name = builder.name;
        this.type = builder.type;
        this.vars = builder.vars;
        this.expr = builder.expr;
    }

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
        return vars != null && !vars.equals("");
    }

    public String getExpr() {
        return expr;
    }

    public Boolean hasExpr() {
        return expr != null && !expr.equals("");
    }

    /**
     * Main class to create an object of {@link CodeParam} using builder pattern
     * It contains set methods returning always a Builder instance
     */
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

        /**
         * returns a new object of {@link CodeParam} with set fields
         */
        public CodeParam build() {
            return new CodeParam(this);
        }
    }
}

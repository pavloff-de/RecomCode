package de.pavloff.pycharm.core;

/** Main class for variables used in {@link CodeFragment}
 * It contains the name (module name), value of the variable and its type
 * It will be used to replace {@link CodeParam} with a value of variable from context
 */
public class CodeVariable {

    /**
     * type of the variable in a context
     */
    private final String type;

    /**
     * name of the parameter which can be replaced by the variable
     */
    private final String name;

    /**
     * the value of the variable in a context
     */
    private final String value;

    /**
     * helps to show right name of the variable in case of renaming of the
     * module like:
     * > import pandas as pd
     *
     * makes only sense if type of variable is 'module'
     */
    private final String moduleName; // if type is 'module'

    // creates an object using builder pattern
    private CodeVariable(Builder builder) {
        type = builder.type;
        name = builder.name;
        value = builder.value;
        moduleName = builder.moduleName;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getModuleName() {
        return moduleName;
    }

    /**
     * Main class to create an object of {@link CodeVariable} using builder pattern
     * It contains set methods returning always a Builder instance
     */
    public static class Builder {

        private String type;
        private String name;
        private String value;

        private String moduleName;

        public Builder setType(String type) {
            this.type = type.toLowerCase();
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setModuleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        /**
         * returns a new object of {@link CodeVariable} with set fields
         */
        public CodeVariable build() {
            return new CodeVariable(this);
        }
    }
}

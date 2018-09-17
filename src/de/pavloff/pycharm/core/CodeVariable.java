package de.pavloff.pycharm.core;

public class CodeVariable {

    private final String type;
    private final String name;
    private final String value;
    private final String moduleName; // if type is 'module'

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

    private CodeVariable(Builder builder) {
        type = builder.type;
        name = builder.name;
        value = builder.value;
        moduleName = builder.moduleName;
    }

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

        public CodeVariable build() {
            return new CodeVariable(this);
        }
    }
}

package de.pavloff.pycharm.core;

public class CodeVariable {

    private final String name;
    private final String type;
    private final String moduleName; // if type is 'module'

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getModuleName() {
        return moduleName;
    }

    private CodeVariable(Builder builder) {
        name = builder.name;
        type = builder.type;
        moduleName = builder.moduleName;
    }

    public static class Builder {

        private String name;
        private String type;
        private String moduleName;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(String type) {
            this.type = type.toLowerCase();
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

package de.pavloff.pycharm.core;

import java.util.ArrayList;

public class CodeParam {

    private final String recID;
    private final String group;
    private final String name;
    private final String type;
    private final ArrayList<String> names;
    private final String parameterType;

    public CodeParam(Builder builder) {
        this.recID = builder.recID;
        this.group = builder.group;
        this.name = builder.name;
        this.type = builder.type;
        this.names = builder.names;
        this.parameterType = builder.parameterType;
    }

    public static class Builder {

        private String recID;
        private String group;
        private String name;
        private String type;
        private ArrayList<String> names;
        private String parameterType;

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

        public Builder setNames(ArrayList<String> names) {
            this.names = names;
            return this;
        }

        public Builder setParameterType(String parameterType) {
            this.parameterType = parameterType;
            return this;
        }

        public CodeParam build() {
            return new CodeParam(this);
        }
    }
}

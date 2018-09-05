package de.pavloff.pycharm.core;

public class CodeVariable {

    private final String name;
    private final String type;
    private final String moduleName; // if type is 'module'

    public CodeVariable(String varName, String varType, String varModuleName) {
        name = varName;
        type = varType;
        moduleName = varModuleName;
    }
}

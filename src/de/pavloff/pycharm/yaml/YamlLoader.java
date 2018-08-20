package de.pavloff.pycharm.yaml;

import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeParam;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.*;

public class YamlLoader implements CodeFragmentLoader {

    private Yaml yamlReader = new Yaml();
    private ArrayList<CodeParam> params;
    private ArrayList<CodeFragment> fragments;

    @Override
    public ArrayList<CodeFragment> getCodeFragments(File[] files) {
        if (fragments == null) {
            load(files);
        }

        return fragments;
    }

    @Override
    public ArrayList<CodeParam> getCodeParams(File[] files) {
        if (params == null) {
            load(files);
        }

        return params;
    }

    private void load(File[] files) {
        if (params == null) {
            params = new ArrayList<>();
        }
        if (fragments == null) {
            fragments = new ArrayList<>();
        }

        if (files == null) {
            loadDefault();
        } else {
            for (File file : files) {
                try {
                    loadFrom(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadDefault() {
        URL resources = YamlLoader.class.getResource("resources");
        FilenameFilter yamlFiles = (dir, name) -> name.endsWith(".yml");
        File[] yamlResources = new File(resources.getPath()).listFiles(yamlFiles);

        if (yamlResources != null) {
            for (File file : yamlResources) {
                try {
                    loadFrom(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //TODO: log
        }
    }

    private void loadFrom(File path) throws FileNotFoundException {
        InputStream yamlFile = new FileInputStream(path);

        Iterable<Object> jamlSections = yamlReader.loadAll(yamlFile);
        for (Object yamlSection : jamlSections) {
            HashMap record = (HashMap) yamlSection;

            if (record.get("recType").equals("params")) {
                CodeParam p = new CodeParam.Builder()
                        .setRecId(castToString(record.get("recID")))
                        .setGroup(castToString(record.get("group")))
                        .setName(castToString(record.get("name")))
                        .setType(castToString(record.get("type")))
                        .setVars(castToString(record.get("vars")))
                        .setExpr(castToString(record.get("expr")))
                        .build();
                params.add(p);

            } else if (record.get("recType").equals("code")) {
                CodeFragment c = new CodeFragment.Builder()
                        .setRecId(castToString(record.get("recID")))
                        .setGroup(castToString(record.get("group")))
                        .setParent(castToString(record.get("parent")))
                        .setRelated(castToStrings(record.get("related")))
                        .setTextkey(castToStrings(record.get("textkey")))
                        .setKeywords(castToStrings(record.get("keywords")))
                        .setSources(castToString(record.get("sources")))
                        .setDocumentation(castToString(record.get("documentation")))
                        .setCode(castToString(record.get("code")))
                        .setParameters(castToParameter(record.get("parameter")))
                        .build();
                fragments.add(c);
            }
        }
    }

    private String castToString(Object str) {
        try {
            return (String) str;
        } catch (ClassCastException ignored) {}

        return "";
    }

    private ArrayList<String> castToStrings(Object list) {
        try {
            return (ArrayList<String>) list;
        } catch (ClassCastException ignored) {}

        ArrayList<String> castedList = new ArrayList<>();
        try {
            castedList.add((String) list);
            return  castedList;
        } catch (ClassCastException ignored) {}

        return castedList;
    }

    private Map<String, CodeParam> castToParameter(Object list) {
        Map<String, CodeParam> params = new HashMap<>();
        if (list == null) {
            return params;
        }

        ArrayList<Map> paramList;
        try {
            paramList = (ArrayList) list;
        } catch (ClassCastException ignored) {
            paramList = new ArrayList<>();
        }

        try {
            for (Map param : paramList) {
                if (!param.containsKey("name")) {
                    continue;
                }

                String name = (String) param.get("name");
                String vars = (String) param.getOrDefault("vars", "");
                String expr = (String) param.getOrDefault("expr", "");


                params.put(name, new CodeParam.Builder()
                    .setName(castToString(name))
                    .setVars(castToString(vars))
                    .setExpr(castToString(expr))
                    .build());
            }
        } catch (ClassCastException ignored) {}

        return params;
    }
}

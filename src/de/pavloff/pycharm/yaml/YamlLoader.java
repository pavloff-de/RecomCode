package de.pavloff.pycharm.yaml;

import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeParam;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
                        .setNames(castToStrings(record.get("names")))
                        .setType(castToString(record.get("type")))
                        .setParameterType(castToString(record.get("parameterType")))
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
                        .setCommentTemplate(castToString(record.get("commentTemplate")))
                        .setSources(castToString(record.get("sources")))
                        .setDocumentation(castToString(record.get("documentation")))
                        .setCode(castToString(record.get("code")))
                        .setParameterValues(castToParameter(record.get("parameter")))
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

    private Map<String,String> castToParameter(Object list) {
        Map<String,String> params = new LinkedHashMap<>();
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
                if (param.containsKey("vars")) {
                    params.put((String) param.get("name"), ((String) param.get("vars")).split(";")[0]);
                } else {
                    params.put((String) param.get("name"), "");
                }
            }
        } catch (ClassCastException ignored) {}

        return params;
    }
}

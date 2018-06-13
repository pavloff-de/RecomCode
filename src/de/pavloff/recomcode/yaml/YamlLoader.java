package de.pavloff.recomcode.yaml;

import de.pavloff.recomcode.core.CodeFragment;
import de.pavloff.recomcode.core.CodeFragmentLoader;
import de.pavloff.recomcode.core.CodeParam;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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

        params = new ArrayList<>();
        fragments = new ArrayList<>();

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
                        .setParameters(castToStrings(record.get("parameters")))
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
        } catch (ClassCastException e) {}

        ArrayList<String> castedList = new ArrayList<>();
        try {
            castedList.add((String) list);
            return  castedList;
        } catch (ClassCastException ignored) {}

        return castedList;
    }
}

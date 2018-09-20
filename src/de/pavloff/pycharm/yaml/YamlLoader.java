package de.pavloff.pycharm.yaml;

import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeParam;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlLoader implements CodeFragmentLoader {

    private Yaml yamlReader = new Yaml();

    private ArrayList<CodeFragment> fragments;

    @Override
    public List<CodeFragment> getCodeFragments() {
        if (fragments == null) {
            load();
        }
        return fragments;
    }

    @Override
    public void load() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }

        URL resources = YamlLoader.class.getResource("resources");
        FilenameFilter yamlFiles = (dir, name) -> name.endsWith(".yml");
        File[] yamlResources = new File(resources.getPath()).listFiles(yamlFiles);

        if (yamlResources == null) {
            return;
        }

        for (File file : yamlResources) {
            try {
                loadFrom(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadFrom(File path) throws FileNotFoundException {
        InputStream yamlFile = new FileInputStream(path);

        Iterable<Object> jamlSections = yamlReader.loadAll(yamlFile);
        Map<String, CodeParam> globalParams = new HashMap<>();

        for (Object yamlSection : jamlSections) {
            HashMap record = (HashMap) yamlSection;

            if (record == null) {
                continue;
            }

            if (record.get("recType").equals("params")) {
                CodeParam p = new CodeParam.Builder()
                        .setRecId(castToString(record.get("recID")))
                        .setGroup(castToString(record.get("group")))
                        .setName(castToString(record.get("name")))
                        .setType(castToString(record.get("type")))
                        .setVars(castToString(record.get("vars")))
                        .setExpr(castToString(record.get("expr")))
                        .build();
                globalParams.put(p.getName(), p);

            } else if (record.get("recType").equals("code")) {
                String code = castToString(record.get("code"));
                Map<String, CodeParam> defaultParams = castToParams(record.get("parameter"));

                CodeFragment c = new CodeFragment.Builder()
                        .setRecId(castToString(record.get("recID")))
                        .setGroup(castToString(record.get("group")))
                        .setParent(castToString(record.get("parent")))
                        .setRelated(castToStrings(record.get("related")))
                        .setTextkeys(castToStrings(record.get("textkey")))
                        .setKeywords(castToStrings(record.get("keywords")))
                        .setSources(castToString(record.get("sources")))
                        .setDocumentation(castToString(record.get("documentation")))
                        .setCode(code)
                        .setDefaultParams(filterParams(
                                parseVariables(code), defaultParams, globalParams))
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

    private Map<String, CodeParam> castToParams(Object list) {
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

    private String[] parseVariables(String code) {
        Set<String> visitedVariables = new HashSet<>();
        Matcher m = Pattern.compile("\\$(.*?)\\$").matcher(code);

        while (m.find()) {
            visitedVariables.add(m.group(1));
        }
        return visitedVariables.toArray(new String[0]);
    }

    private Map<String, CodeParam> filterParams(String[] variables, Map<String, CodeParam> defaultParams, Map<String, CodeParam> globalParams) {
        Map<String, CodeParam> params = new HashMap<>();

        for (String var : variables) {
            if (globalParams.containsKey(var)) {
                params.put(var, globalParams.get(var));
            }
            if (defaultParams.containsKey(var)) {
                params.put(var, defaultParams.get(var));
            }
        }
        return params;
    }
}

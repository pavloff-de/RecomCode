package de.pavloff.pycharm.plugin;

import com.intellij.openapi.diagnostic.Logger;
import de.pavloff.pycharm.BaseUtils;
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

/** Parser for Yaml files with code fragments and params
 * It reads the Yaml files from resources and creates the objects of
 * {@link CodeFragment} and {@link CodeParam}
 */
public class YamlLoader implements CodeFragmentLoader {

    private Yaml yamlReader = new Yaml();

    private ArrayList<CodeFragment> fragments;

    private static Logger logger = Logger.getInstance(YamlLoader.class);

    private void initialize() {
        if (fragments == null) {
            logger.debug("initializing..");
            fragments = new ArrayList<>();
        }
    }

    @Override
    public List<CodeFragment> getCodeFragments() {
        if (fragments == null) {
            load();
        }
        return fragments;
    }

    @Override
    public void load() {
        initialize();

        logger.debug("loading yaml files..");
        URL resources = BaseUtils.getResource("/yaml");
        FilenameFilter yamlFiles = (dir, name) -> name.endsWith(".yml");
        File[] yamlResources = new File(resources.getPath()).listFiles(yamlFiles);

        if (yamlResources == null) {
            logger.debug(String.format("no resource on '%s' found!", resources.getPath()));
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
        initialize();

        logger.debug(String.format("loading sections from '%s'..", path.getPath()));
        InputStream yamlFile = new FileInputStream(path);
        Iterable<Object> yamlSections = yamlReader.loadAll(yamlFile);
        Map<String, CodeParam> globalParams = new HashMap<>();

        for (Object yamlSection : yamlSections) {
            HashMap record = (HashMap) yamlSection;

            if (record == null) {
                logger.debug("..empty section");
                continue;
            }

            if (record.get("recType").equals("params")) {
                logger.debug("..param section");
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
                logger.debug("..code section");
                String code = castToString(record.get("code"));
                Map<String, CodeParam> defaultParams = castToParams(record.get("parameter"));
                String[] paramsList = parseVariables(code);

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
                        .setParamsList(paramsList)
                        .setDefaultParams(filterParams(paramsList, defaultParams, globalParams))
                        .build();
                fragments.add(c);
            }
        }
    }

    /**
     * translates yaml object to a string
     */
    private String castToString(Object str) {
        try {
            return (String) str;
        } catch (ClassCastException ignored) {}

        return "";
    }

    /**
     * translates yaml object to a list of string
     */
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

    /**
     * translates yaml object to code param
     */
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

    /**
     * looks for variables in code
     * a variable should be surrounded with dollar sign like $VAR$
     */
    private String[] parseVariables(String code) {
        Set<String> visitedVariables = new LinkedHashSet<>();
        Matcher m = Pattern.compile("\\$(.*?)\\$").matcher(code);

        while (m.find()) {
            visitedVariables.add(m.group(1));
        }
        return visitedVariables.toArray(new String[0]);
    }

    /**
     * returns code params proper to the variables from a code fragment
     */
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

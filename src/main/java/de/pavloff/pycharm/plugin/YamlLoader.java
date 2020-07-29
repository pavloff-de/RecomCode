package de.pavloff.pycharm.plugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import de.pavloff.pycharm.BaseUtils;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentLoader;
import de.pavloff.pycharm.core.CodeParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parser for Yaml files with code fragments and params
 * It reads the Yaml files from resources and creates the objects of
 * {@link CodeFragment} and {@link CodeParam}
 */
@State(name = "YamlLoader", storages = {@Storage("codeFragments.xml")})
public class YamlLoader implements CodeFragmentLoader,
        PersistentStateComponent<YamlLoader.State> {

    private Yaml yamlReader = new Yaml();

    private String[] loadedFiles = new String[0];

    private ArrayList<CodeFragment> fragments;

    private static Logger logger = Logger.getInstance(YamlLoader.class);

    public YamlLoader() {
        logger.debug("initializing..");
        loadDefault();
    }

    public static YamlLoader getInstance(Project project) {
        return project.getComponent(YamlLoader.class);
    }

    /**
     * returns a list of loaded CodeFragments
     */
    @Override
    public List<CodeFragment> getCodeFragments() {
        return fragments;
    }

    /**
     * returns a map of loaded CodeFragments on their ids
     */
    @Override
    public Map<String, CodeFragment> getCodeFragmentsWithID() {
        Map<String, CodeFragment> fragmentMap = new HashMap<>();
        for (CodeFragment fragment : fragments) {
            fragmentMap.put(fragment.getRecID(), fragment);
        }
        return fragmentMap;
    }

    /**
     * clears list of CodeFragments to load new one
     */
    @Override
    public void clearCodeFragments() {
        fragments = new ArrayList<>();
    }

    /**
     * loads default CodeFragments
     */
    @Override
    public void loadDefault() {
        logger.debug("loading default yaml files..");
        clearCodeFragments();

        try {
            loadFrom((InputStream) BaseUtils.getResource(
                    "/yaml/fragments-pandas.yml").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loads CodeFragments from input stream
     */
    @Override
    public void loadFrom(InputStream yamlFile) {
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
                        .setSubgroup(castToStrings(record.get("subgroup")))
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

    @Nullable
    @Override
    public State getState() {
        return new State(loadedFiles);
    }

    @Override
    public void loadState(@NotNull State state) {
        loadedFiles = state.loadedFiles;

        if (loadedFiles.length == 0) {
            loadDefault();

        } else {
            for (String loadedFile : loadedFiles) {
                try {
                    loadFrom(new FileInputStream(new File(loadedFile)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** State for Yaml loader
     * It persists the Yaml files to load CodeFragments after restart
     */
    public static final class State {

        public String[] loadedFiles;

        public State() {
            loadedFiles = new String[0];
        }

        public State(String[] toPersist) {
            loadedFiles = toPersist;
        }
    }
}

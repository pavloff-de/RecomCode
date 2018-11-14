package de.pavloff.pycharm;


import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditorLocation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.net.URL;

/** class contains common function to handle with python code and output
 */
public class BaseUtils {

    // separates the output from python code and variables defined in code
    public static String VAR_VIEWER_SEP = "### var viewer output ###";

    // unix line separator
    public static String LINE_SEP = "\n";

    // escaped unix line separator used in strings
    public static String LINE_SEP_ESC = "\\n";

    // default delimiter for CSV
    public static String DELIMITER = ",";

    // name of tab containing output from code
    public static String OUTPUT_TAB = "Output";

    /**
     * guesses the type behind a value by parsing value to Integer Float or Boolean
     */
    public static Class guessValueType(String value) {
        try {
            // parse first Integer as a subclass of Float
            Integer.valueOf(value);
            return Integer.class;
        } catch (NumberFormatException ignored) {
        }

        try {
            Float.valueOf(value);
            return Float.class;
        } catch (NumberFormatException ignored) {
        }

        if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
            return Boolean.class;
        }

        return null;
    }

    /**
     * guesses if CSV has a header by comparing the first two lines
     * CSV has no header if guessed types of both first lines are equal
     * returns -1 if header not found and 0 as an index of line containing header
     */
    public static int hasHeader(String[] lines) {
        if (lines.length == 1) {
            return 0;
        }

        String[] vals1 = lines[0].split(DELIMITER);
        String[] vals2 = lines[1].split(DELIMITER);

        if (vals1.length != vals2.length) {
            return -1;
        }

        int numOfMatches = 0;
        for (int i = 0; i < vals1.length; i++) {
            if (guessValueType(vals1[i]) == guessValueType(vals2[i])) {
                numOfMatches += 1;
            }
        }

        if (numOfMatches != vals1.length) {
            // different values in first two lines
            // points at a header in first line
            return 0;
        }

        return -1;
    }

    /**
     * returns the currently opened editor
     */
    public static FileEditor getOpenedEditor(Project project) {
        FileEditor[] editors =
                FileEditorManager.getInstance(project).getSelectedEditors();

        if (editors.length == 1) {
            return editors[0];
        }

        return null;
    }

    /**
     * returns the file from currently opened editor
     */
    public static VirtualFile getOpenedFile(Project project) {
        FileEditor editor = getOpenedEditor(project);

        if (editor == null) {
            return null;
        }
        return editor.getFile();
    }

    /**
     * returns the position of cursor in the file from currently opened editor
     */
    public static int getCursorPosition(Project project) {
        FileEditor editor = getOpenedEditor(project);
        if (editor == null) {
            return -1;
        }

        TextEditorLocation location = (TextEditorLocation) editor.getCurrentLocation();
        if (location == null) {
            return -1;
        }

        return location.getPosition().line;
    }

    /**
     * finds and returns the URL of resource by his name
     */
    public static URL getResource(String name) {
        return BaseUtils.class.getResource(name);
    }
}

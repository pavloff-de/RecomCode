package de.pavloff.pycharm;


import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditorLocation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.net.URL;

public class BaseUtils {

    public static String VAR_VIEWER_SEP = "### var viewer output ###";
    public static String LINE_SEP = "\n";
    public static String LINE_SEP_ESC = "\\n";
    public static String DELIMITER = ",";
    public static String OUTPUT_TAB = "Output";

    public static Class guessValueType(String value) {
        try {
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

    public static int hasHeader(String[] lines) {
        // -1 if not found
        // 0 if names in the first line
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

    public static FileEditor getOpenedEditor(Project project) {
        FileEditor[] editors =
                FileEditorManager.getInstance(project).getSelectedEditors();

        if (editors.length == 1) {
            return editors[0];
        }

        return null;
    }

    public static VirtualFile getOpenedFile(Project project) {
        FileEditor editor = getOpenedEditor(project);

        if (editor == null) {
            return null;
        }
        return editor.getFile();
    }

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

    public static URL getResource(String name) {
        return BaseUtils.class.getResource(name);
    }
}

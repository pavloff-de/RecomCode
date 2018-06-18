package de.pavloff.pycharm;

import de.pavloff.pycharm.plugin.BaseConstants;

public class BaseUtils implements BaseConstants{

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
}

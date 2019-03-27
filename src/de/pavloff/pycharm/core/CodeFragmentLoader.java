package de.pavloff.pycharm.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/** Main interface for a code fragments and params loader
 */
public interface CodeFragmentLoader {

    // loads code fragments and params from default yaml files
    void loadDefault();

    // loads code fragments and params from specific yaml files
    void loadFrom(File file) throws FileNotFoundException;

    // removes fragments e.g. before load from new one
    void clearCodeFragments();

    // returns the list of code fragments
    // code fragments contains already all needed code params
    List<CodeFragment> getCodeFragments();

    // returns the code fragments as a map
    // fragmentID -> fragment
    Map<String, CodeFragment> getCodeFragmentsWithID();
}

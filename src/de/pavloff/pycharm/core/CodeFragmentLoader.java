package de.pavloff.pycharm.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/** Main interface for a code fragments and params loader
 */
public interface CodeFragmentLoader {

    // loads code fragments and params from default yaml files
    void load();

    // loads code fragments and params from specific yaml files
    void loadFrom(File file) throws FileNotFoundException;

    // returns the list of code fragments
    // code fragments contains already all needed code params
    List<CodeFragment> getCodeFragments();
}

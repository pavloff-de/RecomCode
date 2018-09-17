package de.pavloff.pycharm.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface CodeFragmentLoader {
    void load();
    void loadFrom(File file) throws FileNotFoundException;
    List<CodeFragment> getCodeFragments();
}

package de.pavloff.recomcode.core;

import java.io.File;
import java.util.ArrayList;

public interface CodeFragmentLoader {

    ArrayList<CodeFragment> getCodeFragments(File[] files);
    ArrayList<CodeParam> getCodeParams(File[] files);
}

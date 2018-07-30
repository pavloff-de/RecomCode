package de.pavloff.pycharm.core;

import java.util.LinkedHashSet;

public interface CodeFragmentListener {

    void onOutput(LinkedHashSet<CodeFragment> fragments);
}

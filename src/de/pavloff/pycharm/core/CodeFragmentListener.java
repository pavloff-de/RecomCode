package de.pavloff.pycharm.core;

import java.util.List;

public interface CodeFragmentListener {

    void onOutput(List<CodeFragment> fragments);
}

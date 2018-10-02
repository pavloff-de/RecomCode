package de.pavloff.pycharm.plugin.server_stub;

import de.pavloff.pycharm.core.CodeFragment;

import java.util.LinkedHashSet;

public interface CodeFragmentListenerStub {

    void onOutput(LinkedHashSet<CodeFragment> fragments);
}

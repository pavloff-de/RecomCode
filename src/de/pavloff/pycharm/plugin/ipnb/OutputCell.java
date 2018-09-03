package de.pavloff.pycharm.plugin.ipnb;

import java.util.List;

public interface OutputCell {

    void onOutput(List<String> output);
    void onPayload(String payload);
    void onError(String eName, String eValue, List<String> traceback);
}
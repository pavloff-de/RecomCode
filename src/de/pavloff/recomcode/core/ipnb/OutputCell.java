package de.pavloff.recomcode.core.ipnb;

import java.util.List;

public interface OutputCell {

    void onOutput(String output);
    void onPayload(String payload);
}

package de.pavloff.pycharm.plugin.ipnb;

import java.util.List;

/** Class simulates the {@link org.jetbrains.plugins.ipnb.format.cells.IpnbCell}
 * It can be used to separate all possible outputs of the code execution
 * @see org.jetbrains.plugins.ipnb.protocol.IpnbConnectionListenerBase for more info
 */
public interface OutputCell {

    void onOutput(List<String> output);
    void onPayload(String payload);
    void onError(String eName, String eValue, List<String> traceback);
}

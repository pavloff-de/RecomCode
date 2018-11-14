package de.pavloff.pycharm.plugin.serverstub;

/**
 * creates an instance of ServerStub
 */
public class ServerStubFactory {

    private static ServerStub INSTANCE;

    // todo: think whether we need a separate instance per project?
    // Not good for RPC, as we have only 1 server process
    // Currently, there is only 1 instance for all projects!
    public static ServerStub getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerStubNoRPC();
            // instantiate the alternative class ServerStubWithRPC for RPC
            // INSTANCE = new ServerStubWithRPC();
        }

        return INSTANCE;
    }
}

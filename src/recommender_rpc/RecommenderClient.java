package recommender_rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client for the {@link RecommenderServer}.
 */
public class RecommenderClient {

    private static final Logger logger = Logger.getLogger(RecommenderClient.class.getName());

    private final ManagedChannel channel;
    private final RecommenderServiceGrpc.RecommenderServiceBlockingStub blockingStub;

    /** Construct client connecting to RecommenderServer server at {@code host:port}. */
    public RecommenderClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build());
    }

    /** Construct client for accessing HelloWorld server using the existing channel. */
    RecommenderClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = RecommenderServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Initializes a new session, and returns sessionID
     *
     * @param prgLanguage
     * @param pathSrcFile
     * @param settingsPath
     * @return sessionId if all went OK, or -1 on failure
     */
    public long initSession(String prgLanguage, String pathSrcFile, String settingsPath) {
        InitialisationReq request = InitialisationReq.newBuilder().
                setSettingsPath(settingsPath).setProgLanguage(prgLanguage).
                setSrcFilePath(pathSrcFile).build();

        InitialisationResp response;
        String status;
        long sessionId;

        logger.info("Trying to create a session for language " + prgLanguage + " ...");
        try {
            response = blockingStub.initSession(request);
            status = response.getStatus();
            sessionId = response.getSessionId();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "Init session RPC failed: {0}", e.getStatus());
            return -1;
        }
        logger.info("Response to init session: " + status + ", with sessionID: " + sessionId);
        return status.equals("OK") ? sessionId : -1;
    }


    /** Closes a session with given sessionId
     *
     * @param sessionId
     * @return true if all ok, otherwise false
     */
    public boolean closeSession(long sessionId) {
        DisposeRecommenderReq request = DisposeRecommenderReq.newBuilder().setSessionId(sessionId).build();

        DisposeRecommenderResp response;
        String status;
        logger.info("Trying to close a session with sessionID: " + sessionId);
        try {
            response = blockingStub.closeSession(request);
            status = response.getStatus();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "Closing session failed: {0}", e.getStatus());
            return false;
        }
        logger.info("Response to closing session: " + status + ", with sessionID: " + sessionId);
        return status.equals("OK");

    }



    private static RecommenderClient client;
    // Creates a instance of a client (singleton object)

    @NotNull
    @Contract("_, _ -> new")
    /** Creates an instance of a {@link RecommenderClient} and stores reference for getOrCreateClient()
     */
    public static RecommenderClient createClient(String host, int port) {
        RecommenderClient.client = new RecommenderClient (host, port);
        return RecommenderClient.client;
    }

    /** Either returns reference to previously created client or
     *  creates a new client with host: "localhost" and port: 50051, and stores ref for further usage.
     * @return Instance of {@link RecommenderClient}
     */
    public static RecommenderClient getOrCreateClient() {
        if (RecommenderClient.client == null) {
            RecommenderClient.client = new RecommenderClient ("localhost", 50051);
        }
        return RecommenderClient.client;
    }

    /**
     * Main, Only for testing
     */
    public static void main(String[] args) throws Exception {
        RecommenderClient client = new RecommenderClient("localhost", 50051);
        String prgLanguage = "Python";
        try {
            /* Access a service running on the local machine on port 50051 */
            long sessionId = client.initSession(prgLanguage, "(no src file so far)", "(no settings)");
            client.closeSession(sessionId);

        } finally {
            client.shutdown();
        }
    }

}

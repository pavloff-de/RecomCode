package recommender_rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client to test the {@link RecommenderServer}.
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

    /** initialize session */
    public void initSession(String prgLanguage, String pathSrcFile) {
        logger.info("Will try to create a session for language " + prgLanguage + " ...");
        InitialisationReq request = InitialisationReq.newBuilder().
                setSettingsPath("(no settings)").setProgLanguage(prgLanguage).
                setSrcFilePath(pathSrcFile).build();
        InitialisationResp response;
        String status;
        try {
            response = blockingStub.initSession(request);
            status = response.getStatus();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Response to init session: " + status);
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        RecommenderClient client = new RecommenderClient("localhost", 50051);
        try {
            /* Access a service running on the local machine on port 50051 */
            String prgLanguage = "Python";
            if (args.length > 0) {
                prgLanguage = args[0]; /* Use the arg as prgLanguage */
            }
            client.initSession(prgLanguage, "(no src file so far)");
        } finally {
            client.shutdown();
        }
    }
}

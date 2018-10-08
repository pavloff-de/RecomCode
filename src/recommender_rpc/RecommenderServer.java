package recommender_rpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Server that implements advanced recommendations.
 */
public class RecommenderServer {
    private static final Logger logger = Logger.getLogger(RecommenderServer.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new RecommenderServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                RecommenderServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }


    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final RecommenderServer server = new RecommenderServer();
        server.start();
        server.blockUntilShutdown();
    }


    /* The implementation of the server core
     */
    static class RecommenderServiceImpl extends RecommenderServiceGrpc.RecommenderServiceImplBase {

        long sessionId = 111;
        @Override
        public void initSession(InitialisationReq req, StreamObserver<InitialisationResp> responseObserver) {
            System.out.println("Server: initialisation request received, data:");
            System.out.println("   Settings path: " + req.getSettingsPath());
            System.out.println("   Programming language: " + req.getProgLanguage());

            InitialisationResp reply = InitialisationResp.newBuilder()
                    .setSessionId(sessionId).setStatus("OK").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void closeSession(DisposeRecommenderReq req, StreamObserver<DisposeRecommenderResp> responseObserver) {
            System.out.println("Server: close session request received, data:");
            System.out.println("   Session id: " + req.getSessionId());
            DisposeRecommenderResp reply = DisposeRecommenderResp.newBuilder().
                    setSessionId(req.getSessionId()).setStatus("OK").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}

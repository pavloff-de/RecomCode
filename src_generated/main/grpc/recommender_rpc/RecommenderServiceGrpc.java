package recommender_rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * The advanced recommender service
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: recom_rpc.proto")
public final class RecommenderServiceGrpc {

  private RecommenderServiceGrpc() {}

  public static final String SERVICE_NAME = "recommender_rpc.RecommenderService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<recommender_rpc.InitialisationReq,
      recommender_rpc.InitialisationResp> getInitSessionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InitSession",
      requestType = recommender_rpc.InitialisationReq.class,
      responseType = recommender_rpc.InitialisationResp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<recommender_rpc.InitialisationReq,
      recommender_rpc.InitialisationResp> getInitSessionMethod() {
    io.grpc.MethodDescriptor<recommender_rpc.InitialisationReq, recommender_rpc.InitialisationResp> getInitSessionMethod;
    if ((getInitSessionMethod = RecommenderServiceGrpc.getInitSessionMethod) == null) {
      synchronized (RecommenderServiceGrpc.class) {
        if ((getInitSessionMethod = RecommenderServiceGrpc.getInitSessionMethod) == null) {
          RecommenderServiceGrpc.getInitSessionMethod = getInitSessionMethod = 
              io.grpc.MethodDescriptor.<recommender_rpc.InitialisationReq, recommender_rpc.InitialisationResp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "recommender_rpc.RecommenderService", "InitSession"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.InitialisationReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.InitialisationResp.getDefaultInstance()))
                  .setSchemaDescriptor(new RecommenderServiceMethodDescriptorSupplier("InitSession"))
                  .build();
          }
        }
     }
     return getInitSessionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<recommender_rpc.DisposeRecommenderReq,
      recommender_rpc.DisposeRecommenderResp> getCloseSessionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CloseSession",
      requestType = recommender_rpc.DisposeRecommenderReq.class,
      responseType = recommender_rpc.DisposeRecommenderResp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<recommender_rpc.DisposeRecommenderReq,
      recommender_rpc.DisposeRecommenderResp> getCloseSessionMethod() {
    io.grpc.MethodDescriptor<recommender_rpc.DisposeRecommenderReq, recommender_rpc.DisposeRecommenderResp> getCloseSessionMethod;
    if ((getCloseSessionMethod = RecommenderServiceGrpc.getCloseSessionMethod) == null) {
      synchronized (RecommenderServiceGrpc.class) {
        if ((getCloseSessionMethod = RecommenderServiceGrpc.getCloseSessionMethod) == null) {
          RecommenderServiceGrpc.getCloseSessionMethod = getCloseSessionMethod = 
              io.grpc.MethodDescriptor.<recommender_rpc.DisposeRecommenderReq, recommender_rpc.DisposeRecommenderResp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "recommender_rpc.RecommenderService", "CloseSession"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.DisposeRecommenderReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.DisposeRecommenderResp.getDefaultInstance()))
                  .setSchemaDescriptor(new RecommenderServiceMethodDescriptorSupplier("CloseSession"))
                  .build();
          }
        }
     }
     return getCloseSessionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<recommender_rpc.OnNewInputReq,
      recommender_rpc.SuggestionsSetResp> getOnUserInputMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "onUserInput",
      requestType = recommender_rpc.OnNewInputReq.class,
      responseType = recommender_rpc.SuggestionsSetResp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<recommender_rpc.OnNewInputReq,
      recommender_rpc.SuggestionsSetResp> getOnUserInputMethod() {
    io.grpc.MethodDescriptor<recommender_rpc.OnNewInputReq, recommender_rpc.SuggestionsSetResp> getOnUserInputMethod;
    if ((getOnUserInputMethod = RecommenderServiceGrpc.getOnUserInputMethod) == null) {
      synchronized (RecommenderServiceGrpc.class) {
        if ((getOnUserInputMethod = RecommenderServiceGrpc.getOnUserInputMethod) == null) {
          RecommenderServiceGrpc.getOnUserInputMethod = getOnUserInputMethod = 
              io.grpc.MethodDescriptor.<recommender_rpc.OnNewInputReq, recommender_rpc.SuggestionsSetResp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "recommender_rpc.RecommenderService", "onUserInput"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.OnNewInputReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.SuggestionsSetResp.getDefaultInstance()))
                  .setSchemaDescriptor(new RecommenderServiceMethodDescriptorSupplier("onUserInput"))
                  .build();
          }
        }
     }
     return getOnUserInputMethod;
  }

  private static volatile io.grpc.MethodDescriptor<recommender_rpc.OnSuggestionSelectedReq,
      recommender_rpc.SuggestionDetailsResp> getSuggestionSelectedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "suggestionSelected",
      requestType = recommender_rpc.OnSuggestionSelectedReq.class,
      responseType = recommender_rpc.SuggestionDetailsResp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<recommender_rpc.OnSuggestionSelectedReq,
      recommender_rpc.SuggestionDetailsResp> getSuggestionSelectedMethod() {
    io.grpc.MethodDescriptor<recommender_rpc.OnSuggestionSelectedReq, recommender_rpc.SuggestionDetailsResp> getSuggestionSelectedMethod;
    if ((getSuggestionSelectedMethod = RecommenderServiceGrpc.getSuggestionSelectedMethod) == null) {
      synchronized (RecommenderServiceGrpc.class) {
        if ((getSuggestionSelectedMethod = RecommenderServiceGrpc.getSuggestionSelectedMethod) == null) {
          RecommenderServiceGrpc.getSuggestionSelectedMethod = getSuggestionSelectedMethod = 
              io.grpc.MethodDescriptor.<recommender_rpc.OnSuggestionSelectedReq, recommender_rpc.SuggestionDetailsResp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "recommender_rpc.RecommenderService", "suggestionSelected"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.OnSuggestionSelectedReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  recommender_rpc.SuggestionDetailsResp.getDefaultInstance()))
                  .setSchemaDescriptor(new RecommenderServiceMethodDescriptorSupplier("suggestionSelected"))
                  .build();
          }
        }
     }
     return getSuggestionSelectedMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RecommenderServiceStub newStub(io.grpc.Channel channel) {
    return new RecommenderServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RecommenderServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RecommenderServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RecommenderServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RecommenderServiceFutureStub(channel);
  }

  /**
   * <pre>
   * The advanced recommender service
   * </pre>
   */
  public static abstract class RecommenderServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void initSession(recommender_rpc.InitialisationReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.InitialisationResp> responseObserver) {
      asyncUnimplementedUnaryCall(getInitSessionMethod(), responseObserver);
    }

    /**
     */
    public void closeSession(recommender_rpc.DisposeRecommenderReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.DisposeRecommenderResp> responseObserver) {
      asyncUnimplementedUnaryCall(getCloseSessionMethod(), responseObserver);
    }

    /**
     */
    public void onUserInput(recommender_rpc.OnNewInputReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.SuggestionsSetResp> responseObserver) {
      asyncUnimplementedUnaryCall(getOnUserInputMethod(), responseObserver);
    }

    /**
     */
    public void suggestionSelected(recommender_rpc.OnSuggestionSelectedReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.SuggestionDetailsResp> responseObserver) {
      asyncUnimplementedUnaryCall(getSuggestionSelectedMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getInitSessionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                recommender_rpc.InitialisationReq,
                recommender_rpc.InitialisationResp>(
                  this, METHODID_INIT_SESSION)))
          .addMethod(
            getCloseSessionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                recommender_rpc.DisposeRecommenderReq,
                recommender_rpc.DisposeRecommenderResp>(
                  this, METHODID_CLOSE_SESSION)))
          .addMethod(
            getOnUserInputMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                recommender_rpc.OnNewInputReq,
                recommender_rpc.SuggestionsSetResp>(
                  this, METHODID_ON_USER_INPUT)))
          .addMethod(
            getSuggestionSelectedMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                recommender_rpc.OnSuggestionSelectedReq,
                recommender_rpc.SuggestionDetailsResp>(
                  this, METHODID_SUGGESTION_SELECTED)))
          .build();
    }
  }

  /**
   * <pre>
   * The advanced recommender service
   * </pre>
   */
  public static final class RecommenderServiceStub extends io.grpc.stub.AbstractStub<RecommenderServiceStub> {
    private RecommenderServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RecommenderServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RecommenderServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RecommenderServiceStub(channel, callOptions);
    }

    /**
     */
    public void initSession(recommender_rpc.InitialisationReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.InitialisationResp> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInitSessionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void closeSession(recommender_rpc.DisposeRecommenderReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.DisposeRecommenderResp> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCloseSessionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void onUserInput(recommender_rpc.OnNewInputReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.SuggestionsSetResp> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getOnUserInputMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void suggestionSelected(recommender_rpc.OnSuggestionSelectedReq request,
        io.grpc.stub.StreamObserver<recommender_rpc.SuggestionDetailsResp> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSuggestionSelectedMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The advanced recommender service
   * </pre>
   */
  public static final class RecommenderServiceBlockingStub extends io.grpc.stub.AbstractStub<RecommenderServiceBlockingStub> {
    private RecommenderServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RecommenderServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RecommenderServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RecommenderServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public recommender_rpc.InitialisationResp initSession(recommender_rpc.InitialisationReq request) {
      return blockingUnaryCall(
          getChannel(), getInitSessionMethod(), getCallOptions(), request);
    }

    /**
     */
    public recommender_rpc.DisposeRecommenderResp closeSession(recommender_rpc.DisposeRecommenderReq request) {
      return blockingUnaryCall(
          getChannel(), getCloseSessionMethod(), getCallOptions(), request);
    }

    /**
     */
    public recommender_rpc.SuggestionsSetResp onUserInput(recommender_rpc.OnNewInputReq request) {
      return blockingUnaryCall(
          getChannel(), getOnUserInputMethod(), getCallOptions(), request);
    }

    /**
     */
    public recommender_rpc.SuggestionDetailsResp suggestionSelected(recommender_rpc.OnSuggestionSelectedReq request) {
      return blockingUnaryCall(
          getChannel(), getSuggestionSelectedMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The advanced recommender service
   * </pre>
   */
  public static final class RecommenderServiceFutureStub extends io.grpc.stub.AbstractStub<RecommenderServiceFutureStub> {
    private RecommenderServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RecommenderServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RecommenderServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RecommenderServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<recommender_rpc.InitialisationResp> initSession(
        recommender_rpc.InitialisationReq request) {
      return futureUnaryCall(
          getChannel().newCall(getInitSessionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<recommender_rpc.DisposeRecommenderResp> closeSession(
        recommender_rpc.DisposeRecommenderReq request) {
      return futureUnaryCall(
          getChannel().newCall(getCloseSessionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<recommender_rpc.SuggestionsSetResp> onUserInput(
        recommender_rpc.OnNewInputReq request) {
      return futureUnaryCall(
          getChannel().newCall(getOnUserInputMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<recommender_rpc.SuggestionDetailsResp> suggestionSelected(
        recommender_rpc.OnSuggestionSelectedReq request) {
      return futureUnaryCall(
          getChannel().newCall(getSuggestionSelectedMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INIT_SESSION = 0;
  private static final int METHODID_CLOSE_SESSION = 1;
  private static final int METHODID_ON_USER_INPUT = 2;
  private static final int METHODID_SUGGESTION_SELECTED = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RecommenderServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RecommenderServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INIT_SESSION:
          serviceImpl.initSession((recommender_rpc.InitialisationReq) request,
              (io.grpc.stub.StreamObserver<recommender_rpc.InitialisationResp>) responseObserver);
          break;
        case METHODID_CLOSE_SESSION:
          serviceImpl.closeSession((recommender_rpc.DisposeRecommenderReq) request,
              (io.grpc.stub.StreamObserver<recommender_rpc.DisposeRecommenderResp>) responseObserver);
          break;
        case METHODID_ON_USER_INPUT:
          serviceImpl.onUserInput((recommender_rpc.OnNewInputReq) request,
              (io.grpc.stub.StreamObserver<recommender_rpc.SuggestionsSetResp>) responseObserver);
          break;
        case METHODID_SUGGESTION_SELECTED:
          serviceImpl.suggestionSelected((recommender_rpc.OnSuggestionSelectedReq) request,
              (io.grpc.stub.StreamObserver<recommender_rpc.SuggestionDetailsResp>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RecommenderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RecommenderServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return recommender_rpc.RecommenderRpcProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RecommenderService");
    }
  }

  private static final class RecommenderServiceFileDescriptorSupplier
      extends RecommenderServiceBaseDescriptorSupplier {
    RecommenderServiceFileDescriptorSupplier() {}
  }

  private static final class RecommenderServiceMethodDescriptorSupplier
      extends RecommenderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RecommenderServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RecommenderServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RecommenderServiceFileDescriptorSupplier())
              .addMethod(getInitSessionMethod())
              .addMethod(getCloseSessionMethod())
              .addMethod(getOnUserInputMethod())
              .addMethod(getSuggestionSelectedMethod())
              .build();
        }
      }
    }
    return result;
  }
}

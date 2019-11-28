package com.treasuremountain.datalake.dlapiservice.service.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.20.0)",
    comments = "Source: hive.proto")
public final class hiveSearchGreeterGrpc {

  private hiveSearchGreeterGrpc() {}

  public static final String SERVICE_NAME = "hiveSearchGreeter";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<hiveSearchRequest,
      hiveSearchResponse> getHiveSearchMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "hiveSearch",
      requestType = hiveSearchRequest.class,
      responseType = hiveSearchResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<hiveSearchRequest,
      hiveSearchResponse> getHiveSearchMethod() {
    io.grpc.MethodDescriptor<hiveSearchRequest, hiveSearchResponse> getHiveSearchMethod;
    if ((getHiveSearchMethod = hiveSearchGreeterGrpc.getHiveSearchMethod) == null) {
      synchronized (hiveSearchGreeterGrpc.class) {
        if ((getHiveSearchMethod = hiveSearchGreeterGrpc.getHiveSearchMethod) == null) {
          hiveSearchGreeterGrpc.getHiveSearchMethod = getHiveSearchMethod = 
              io.grpc.MethodDescriptor.<hiveSearchRequest, hiveSearchResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "hiveSearchGreeter", "hiveSearch"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  hiveSearchRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  hiveSearchResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new hiveSearchGreeterMethodDescriptorSupplier("hiveSearch"))
                  .build();
          }
        }
     }
     return getHiveSearchMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static hiveSearchGreeterStub newStub(io.grpc.Channel channel) {
    return new hiveSearchGreeterStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static hiveSearchGreeterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new hiveSearchGreeterBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static hiveSearchGreeterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new hiveSearchGreeterFutureStub(channel);
  }

  /**
   */
  public static abstract class hiveSearchGreeterImplBase implements io.grpc.BindableService {

    /**
     */
    public void hiveSearch(hiveSearchRequest request,
        io.grpc.stub.StreamObserver<hiveSearchResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHiveSearchMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getHiveSearchMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                hiveSearchRequest,
                hiveSearchResponse>(
                  this, METHODID_HIVE_SEARCH)))
          .build();
    }
  }

  /**
   */
  public static final class hiveSearchGreeterStub extends io.grpc.stub.AbstractStub<hiveSearchGreeterStub> {
    private hiveSearchGreeterStub(io.grpc.Channel channel) {
      super(channel);
    }

    private hiveSearchGreeterStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected hiveSearchGreeterStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new hiveSearchGreeterStub(channel, callOptions);
    }

    /**
     */
    public void hiveSearch(hiveSearchRequest request,
        io.grpc.stub.StreamObserver<hiveSearchResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getHiveSearchMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class hiveSearchGreeterBlockingStub extends io.grpc.stub.AbstractStub<hiveSearchGreeterBlockingStub> {
    private hiveSearchGreeterBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private hiveSearchGreeterBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected hiveSearchGreeterBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new hiveSearchGreeterBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<hiveSearchResponse> hiveSearch(
        hiveSearchRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getHiveSearchMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class hiveSearchGreeterFutureStub extends io.grpc.stub.AbstractStub<hiveSearchGreeterFutureStub> {
    private hiveSearchGreeterFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private hiveSearchGreeterFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected hiveSearchGreeterFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new hiveSearchGreeterFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_HIVE_SEARCH = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final hiveSearchGreeterImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(hiveSearchGreeterImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HIVE_SEARCH:
          serviceImpl.hiveSearch((hiveSearchRequest) request,
              (io.grpc.stub.StreamObserver<hiveSearchResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class hiveSearchGreeterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    hiveSearchGreeterBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return hiveGrpc.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("hiveSearchGreeter");
    }
  }

  private static final class hiveSearchGreeterFileDescriptorSupplier
      extends hiveSearchGreeterBaseDescriptorSupplier {
    hiveSearchGreeterFileDescriptorSupplier() {}
  }

  private static final class hiveSearchGreeterMethodDescriptorSupplier
      extends hiveSearchGreeterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    hiveSearchGreeterMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (hiveSearchGreeterGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new hiveSearchGreeterFileDescriptorSupplier())
              .addMethod(getHiveSearchMethod())
              .build();
        }
      }
    }
    return result;
  }
}

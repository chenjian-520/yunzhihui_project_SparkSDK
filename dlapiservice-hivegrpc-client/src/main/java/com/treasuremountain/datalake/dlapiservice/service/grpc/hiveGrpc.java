// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: hive.proto

package com.treasuremountain.datalake.dlapiservice.service.grpc;

public final class hiveGrpc {
  private hiveGrpc() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_hiveSearchRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_hiveSearchRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_hiveSearchResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_hiveSearchResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\nhive.proto\"7\n\021hiveSearchRequest\022\013\n\003sql" +
      "\030\001 \001(\t\022\025\n\rextJsonString\030\002 \001(\t\"M\n\022hiveSea" +
      "rchResponse\022\020\n\010dataJson\030\001 \001(\t\022\017\n\007isError" +
      "\030\002 \001(\010\022\024\n\014errorMessage\030\003 \001(\t2N\n\021hiveSear" +
      "chGreeter\0229\n\nhiveSearch\022\022.hiveSearchRequ" +
      "est\032\023.hiveSearchResponse\"\0000\001BE\n7com.trea" +
      "suremountain.datalake.dlapiservice.servi" +
      "ce.grpcB\010hiveGrpcP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_hiveSearchRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_hiveSearchRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_hiveSearchRequest_descriptor,
        new String[] { "Sql", "ExtJsonString", });
    internal_static_hiveSearchResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_hiveSearchResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_hiveSearchResponse_descriptor,
        new String[] { "DataJson", "IsError", "ErrorMessage", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}

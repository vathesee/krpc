// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: pushservice.proto

package com.xxx.pushservice.proto;

public final class PushServiceMetas {
  private PushServiceMetas() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_PushReq_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_PushReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_PushRes_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_PushRes_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021pushservice.proto\032\rkrpcext.proto\",\n\007Pu" +
      "shReq\022\020\n\010clientId\030\001 \001(\t\022\017\n\007message\030\002 \001(\t" +
      "\"*\n\007PushRes\022\017\n\007retCode\030\001 \001(\005\022\016\n\006retMsg\030\002" +
      " \001(\t23\n\013PushService\022\037\n\004push\022\010.PushReq\032\010." +
      "PushRes\"\003\320>\001\032\003\310>e27\n\rPushServicev2\022!\n\006pu" +
      "shv2\022\010.PushReq\032\010.PushRes\"\003\320>\001\032\003\310>fB2\n\031co" +
      "m.xxx.pushservice.protoB\020PushServiceMeta" +
      "sP\001\210\001\001b\006proto3"
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
          krpc.KrpcExt.getDescriptor(),
        }, assigner);
    internal_static_PushReq_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_PushReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_PushReq_descriptor,
        new java.lang.String[] { "ClientId", "Message", });
    internal_static_PushRes_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_PushRes_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_PushRes_descriptor,
        new java.lang.String[] { "RetCode", "RetMsg", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(krpc.KrpcExt.msgId);
    registry.add(krpc.KrpcExt.serviceId);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    krpc.KrpcExt.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}

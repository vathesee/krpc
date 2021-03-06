// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: krpcmonitor.proto

package krpc.rpc.monitor.proto;

public interface ReportMetaReqOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ReportMetaReq)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 timestamp = 1;</code>
   */
  long getTimestamp();

  /**
   * <code>string host = 2;</code>
   */
  java.lang.String getHost();
  /**
   * <code>string host = 2;</code>
   */
  com.google.protobuf.ByteString
      getHostBytes();

  /**
   * <code>string app = 3;</code>
   */
  java.lang.String getApp();
  /**
   * <code>string app = 3;</code>
   */
  com.google.protobuf.ByteString
      getAppBytes();

  /**
   * <code>int32 appServiceId = 4;</code>
   */
  int getAppServiceId();

  /**
   * <code>repeated .MetaInfo info = 5;</code>
   */
  java.util.List<krpc.rpc.monitor.proto.MetaInfo> 
      getInfoList();
  /**
   * <code>repeated .MetaInfo info = 5;</code>
   */
  krpc.rpc.monitor.proto.MetaInfo getInfo(int index);
  /**
   * <code>repeated .MetaInfo info = 5;</code>
   */
  int getInfoCount();
  /**
   * <code>repeated .MetaInfo info = 5;</code>
   */
  java.util.List<? extends krpc.rpc.monitor.proto.MetaInfoOrBuilder> 
      getInfoOrBuilderList();
  /**
   * <code>repeated .MetaInfo info = 5;</code>
   */
  krpc.rpc.monitor.proto.MetaInfoOrBuilder getInfoOrBuilder(
      int index);
}

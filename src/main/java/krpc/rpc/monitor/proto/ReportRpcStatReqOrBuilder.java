// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: krpcmonitor.proto

package krpc.rpc.monitor.proto;

public interface ReportRpcStatReqOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ReportRpcStatReq)
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
   * <code>repeated .RpcStat stats = 4;</code>
   */
  java.util.List<krpc.rpc.monitor.proto.RpcStat> 
      getStatsList();
  /**
   * <code>repeated .RpcStat stats = 4;</code>
   */
  krpc.rpc.monitor.proto.RpcStat getStats(int index);
  /**
   * <code>repeated .RpcStat stats = 4;</code>
   */
  int getStatsCount();
  /**
   * <code>repeated .RpcStat stats = 4;</code>
   */
  java.util.List<? extends krpc.rpc.monitor.proto.RpcStatOrBuilder> 
      getStatsOrBuilderList();
  /**
   * <code>repeated .RpcStat stats = 4;</code>
   */
  krpc.rpc.monitor.proto.RpcStatOrBuilder getStatsOrBuilder(
      int index);

  /**
   * <code>int32 appServiceId = 5;</code>
   */
  int getAppServiceId();
}

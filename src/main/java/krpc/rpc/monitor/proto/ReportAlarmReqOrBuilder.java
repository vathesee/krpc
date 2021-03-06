// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: krpcmonitor.proto

package krpc.rpc.monitor.proto;

public interface ReportAlarmReqOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ReportAlarmReq)
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
   * <code>repeated .AlarmInfo info = 4;</code>
   */
  java.util.List<krpc.rpc.monitor.proto.AlarmInfo> 
      getInfoList();
  /**
   * <code>repeated .AlarmInfo info = 4;</code>
   */
  krpc.rpc.monitor.proto.AlarmInfo getInfo(int index);
  /**
   * <code>repeated .AlarmInfo info = 4;</code>
   */
  int getInfoCount();
  /**
   * <code>repeated .AlarmInfo info = 4;</code>
   */
  java.util.List<? extends krpc.rpc.monitor.proto.AlarmInfoOrBuilder> 
      getInfoOrBuilderList();
  /**
   * <code>repeated .AlarmInfo info = 4;</code>
   */
  krpc.rpc.monitor.proto.AlarmInfoOrBuilder getInfoOrBuilder(
      int index);

  /**
   * <code>int32 appServiceId = 5;</code>
   */
  int getAppServiceId();
}

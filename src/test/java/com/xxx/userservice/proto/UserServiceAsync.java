// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: userservice.proto

package com.xxx.userservice.proto;

public interface UserServiceAsync {

    static final public int serviceId = 100;

    java.util.concurrent.CompletableFuture<com.xxx.userservice.proto.LoginRes> login(com.xxx.userservice.proto.LoginReq req);
    static final public int loginMsgId = 1;

    java.util.concurrent.CompletableFuture<com.xxx.userservice.proto.UpdateProfileRes> updateProfile(com.xxx.userservice.proto.UpdateProfileReq req);
    static final public int updateProfileMsgId = 2;

    java.util.concurrent.CompletableFuture<com.xxx.userservice.proto.Login2Res> login2(com.xxx.userservice.proto.Login2Req req);
    static final public int login2MsgId = 3;

}


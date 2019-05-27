package com.xxx.iot.tcp.codec;

import static com.xxx.iot.tcp.codec.MsgConsts.HEAD_DATA;

/**
 * @Author: cdp
 * @Date: 2019/5/15 13:30
 * @Version 1.0
 */
public class MsgProtocol {

    /** 消息头长度 **/
    public static final int HEADER_LEN = 10;

    private int headData = HEAD_DATA;           // 消息头标志
    private int msgBodyLen;         // 消息体长度
    private byte version;           // 消息版本
    public byte flags;              // 消息加密方式
    transient public short cc;      // 校验码
    private int sessionId;          // 会话id
    transient public byte[] body;   // 消息体

    public MsgProtocol() {

    }


    public int getHeadData() {
        return headData;
    }

    public void setHeadData(int headData) {
        this.headData = headData;
    }

    public static int getHeaderLen() {
        return HEADER_LEN;
    }

    public int getMsgBodyLen() {
        return msgBodyLen;
    }

    public void setMsgBodyLen(int msgBodyLen) {
        this.msgBodyLen = msgBodyLen;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public short getCc() {
        return cc;
    }

    public void setCc(short cc) {
        this.cc = cc;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}

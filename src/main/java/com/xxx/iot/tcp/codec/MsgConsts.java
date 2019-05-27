package com.xxx.iot.tcp.codec;

/**
 * @Author: cdp
 * @Date: 2019/5/15 14:25
 * @Version 1.0
 */
public class MsgConsts {

    public static final byte HEAD_DATA = (byte)0xff;
    public static final int  MAX_BODY_LEN = 1024;
    public static final byte HEADER_LEN = 12;

    public static final byte HERT_VERSION = -1;
    public static final byte HEART_MSG = -33;
}

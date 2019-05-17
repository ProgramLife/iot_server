package com.xxx.iot.common.observer;

/**
 * 事件类别
 * Created by Administrator on 2019/1/12.
 */

public enum EventType {
    ON_START,               // 服务开启
    ON_CLOSE,               // 服务关闭
    ON_CONNECT,             // 连接
    ON_READ,                // 读取数据
    ON_DISCONNECT,          // 断开
    ON_SEND,                // 发送

}

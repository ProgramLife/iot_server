package com.xxx.iot.client;

import com.xxx.iot.common.BaseThred;

/**
 * Created by Administrator on 2019/1/9.
 */

public class TcpClientManager {
    /** 未收到服务端数据最大时间间隔 **/
    private static final long MAX_NOT_RECIVE_DATA_TIME = 300 * 1000L;
    /** 断线重连最大时间间隔 **/
    private static final long MAX_RECONNECT_TIME = 30 * 1000L;
    /** 服务端IP **/
    private String ip;
    /** 服务端端口号 **/
    private int port;
    /** 连接超时时间 （默认30秒）**/
    private int conTimeOut = 30;
    /** 客户端线程 **/
    private TcpClientThread tcpClientThread;
    /** 客户端管理类监听器 **/
    private final TcpClientManagerListener tcpClientManagerListener;
    /** 心跳信号量开关 **/
    private volatile boolean mHeatBeatRunFlag = false;
    /** 心跳间隔 （单位为秒） **/
    private final int mHeatBeatTime = 30;
    /** 上次收到数据时间戳 **/
    private long mLastReciveTime;
    /** 连接断开时间戳 **/
    private long mLastDisConnectTime;
    /** 当前时间戳 **/
    private long mCurrentTime;
    /** 是否断线重连，默认开启 **/
    private boolean mReconnectionFlag = true;
    /** 心跳消息 **/
    private final byte[] HEART_RESPONSE = {0x02, 0x20, 0x00, 0x07, 0x01, 0x0d, 0x0a};
    /** 心跳线程 **/
    private TcpHeartbeatThread mTcpHeartbeatThread;
    /** 客户端监听器 **/
    private TcpClientThread.TcpClientListener tcpClientListener = new TcpClientThread.TcpClientListener() {
        @Override
        public void onConnected() {
//            System.out.println("连接成功");
            if (tcpClientManagerListener != null) {
                tcpClientManagerListener.onConnected();
            }
        }

        @Override
        public void onReceived(int bodyLen, byte[] buff) {
//            System.out.println("收到消息" + nBytes);
            mLastReciveTime = System.currentTimeMillis();
            if (tcpClientManagerListener != null) {
                tcpClientManagerListener.onReceived(bodyLen, buff);
            }
        }

        @Override
        public void onDisconnected() {
            mLastDisConnectTime = System.currentTimeMillis();
            if (tcpClientManagerListener != null) {
                tcpClientManagerListener.onDisconnected();
            }
        }

        @Override
        public void onConnectFail() {
            System.out.println("连接失败");
            if (tcpClientManagerListener != null) {
                tcpClientManagerListener.onConnectFailed();
            }
        }
        
        @Override
        public void onSend(int bodyLen, byte[] buff) {
            if (tcpClientManagerListener != null) {
                tcpClientManagerListener.onSend(bodyLen, buff);
            }
        }
    };

    public TcpClientManager(String ip, int port, TcpClientManagerListener tcpClientManagerListener) {
        this.ip = ip;
        this.port = port;
        this.tcpClientManagerListener = tcpClientManagerListener;
    }

    public TcpClientManager(String ip, int port, int conTimeOut, TcpClientManagerListener tcpClientManagerListener) {
        this.ip = ip;
        this.port = port;
        this.conTimeOut = conTimeOut;
        this.tcpClientManagerListener = tcpClientManagerListener;
    }


    /**
     * 开启客户端
     */
    public void startClient() {
        if (tcpClientThread != null) {
            tcpClientThread.stopClient();
        }
        tcpClientThread = null;
        tcpClientThread = new TcpClientThread(ip, port, conTimeOut, tcpClientListener);
        tcpClientThread.startThread();
    }

    /**
     * 关闭客户端
     */
    public void stopClient() {
        stopHeartbeat();
        if (tcpClientThread != null) {
            tcpClientThread.stopClient();
            tcpClientThread = null;
        }
    }

    public boolean isConnected() {
        return this.tcpClientThread != null ? this.tcpClientThread.isConnected() : false;
    }


    public class TcpHeartbeatThread extends BaseThred {

        @Override
        public void run() {
            super.run();
            while (true) {
                if (!isRunFlag()) {
                    break;
                }
                try {
                    Thread.sleep(mHeatBeatTime * 1000);
                    sendHeartBeat();
                    // 如果tcp客户端线程未启动，或者tcp未连接，跳到下个循环
                    if (tcpClientThread == null || !tcpClientThread.isConnected()) {
                        continue;
                    }
                    mCurrentTime = System.currentTimeMillis();
                    sendHeartBeat();
                    // 约定间隔内未收到服务端消息，则关闭tcp客户端线程
                    if ((mCurrentTime - mLastReciveTime) >= MAX_NOT_RECIVE_DATA_TIME) {
                        tcpClientThread.stopClient();
                    }
                    // 如果未开启断线重连，跳到下个循环
                    if (!TcpClientManager.this.mReconnectionFlag) {
                        continue;
                    }
                    // 已开启断线重连、在断线时间超过重连约定时间，则重新连接tcp服务端
                    if ((mCurrentTime - mLastDisConnectTime) >= MAX_RECONNECT_TIME) {
                        TcpClientManager.this.startClient();
                    }
                    // 进入下个轮询
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 启动心跳
     */
    public void startHeartbeat() {
        if (mTcpHeartbeatThread != null) {
            mTcpHeartbeatThread.stopThread();
            mTcpHeartbeatThread = null;
        }
        mTcpHeartbeatThread = new TcpHeartbeatThread();
        mTcpHeartbeatThread.startThread();

    }

    /**
     * 关闭心跳
     */
    public void stopHeartbeat() {
        if (mTcpHeartbeatThread != null) {
            mTcpHeartbeatThread.stopThread();
        }
        mTcpHeartbeatThread = null;
    }

    /**
     * 发送心跳
     */
    public void sendHeartBeat() {
    	send(HEART_RESPONSE);
    	tcpClientListener.onSend(13, HEART_RESPONSE);
    }

    /**
     * 写消息
     * @param msgStr
     * @return
     */
    public boolean writeMsg(String msgStr) {
        if (msgStr == null || msgStr.length() < 1) {
            return false;
        }
        byte[] buff = msgStr.getBytes();
        return this.send(buff);
    }

    public synchronized boolean send(byte[] buff) {
        return tcpClientThread != null ? tcpClientThread.send(buff) : false;
    }

    public interface TcpClientManagerListener {
        public void onConnected();
        public void onReceived(int bodyLen, byte[] buff);
        public void onSend(int bodyLen, byte[] buff);
        public void onDisconnected();
        public void onConnectFailed();
    }
}

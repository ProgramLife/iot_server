package com.xxx.iot.client;

import com.xxx.iot.common.BaseThred;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Administrator on 2019/1/8.
 */

public class TcpClientThread extends BaseThred {

    private static final int MSG_BASE_LEN = 12;
    private static final int BUFF_SIZE = 1024;

    private String ip;
    private int port;
    private Socket socket = null;
    private TcpClientListener tcpClientListener;
    private int conTimeOut = 32 * 1000;

    private InputStream input = null;
    private OutputStream output = null;
    private byte[] readData = new byte[BUFF_SIZE];
    /** tcp 连接标志 */
    private volatile boolean connFlag = false;
    /** io资源是否关闭释放 */
    private volatile boolean closeFlag = false;

    public TcpClientThread(String ip, int port, TcpClientListener tcpClientListener) {
        this.ip = ip;
        this.port = port;
        this.tcpClientListener = tcpClientListener;
    }

    public TcpClientThread(String ip, int port, int connectTimeOut, TcpClientListener tcpClientListener) {
        this.ip = ip;
        this.port = port;
        this.conTimeOut = connectTimeOut;
        this.tcpClientListener = tcpClientListener;
    }

    @Override
    public void run() {
        try {
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            socket.connect(socketAddress, conTimeOut);
            input = socket.getInputStream();
            output = socket.getOutputStream();

            connFlag = true;
//            System.out.println("client start");
            if (tcpClientListener != null) {
//                System.out.println("client onConnected");
                tcpClientListener.onConnected();
            }

            while (isRunFlag() && connFlag) {
                try {
                    if (input == null) { break; }
                    if (output == null) { break; }
                    if (socket.isClosed()) { break; }
                    if (input.available() < MSG_BASE_LEN) { continue; }
                    int length = input.read(readData);
                    if (length < 0){
                        closeAndFree();
                        break;
                    }

                    if (length > 0){
                        // 接收数据
                        byte [] receiveData = new byte[length];
                        // 把接收到信息拷贝到readData数组中
                        System.arraycopy(readData, 0, receiveData, 0, length);
                        if (tcpClientListener != null) {
                            tcpClientListener.onReceived(length, receiveData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAndFree();
        }
    }

    private synchronized void closeAndFree() {
        readData = null;
        try {
            if (input != null) {
                input.close();
                input = null;
            }
            if (output != null) {
                output.close();
                output = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            setRunFlag(false);
            if (socket != null) {
                socket.close();
            }

            if (closeFlag) {
                return;
            }

            closeFlag = true;
            if (tcpClientListener == null) {
                return;
            }

            if (connFlag) {
                tcpClientListener.onDisconnected();
            } else {
                tcpClientListener.onConnectFail();
            }

        } catch (Exception e) {

        } finally {
            closeFlag = true;
            connFlag = false;
            interrupt();
        }
    }

    /**
     * 发送数据
     * @param buff
     * @return
     */
    public boolean send(byte[] buff) {
        if (buff == null) {
            return false;
        }
        if (output != null) {
            try {
                output.write(buff);
//                mTcpClientListener.onSend(buff.length, buff);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void startClient() {

    }

    public void stopClient() {
        try {
            closeAndFree();
            setRunFlag(false);
            interrupt();
        } catch (Exception e) {

        }
    }

    public boolean isConnected() {
        return this.connFlag;
    }



    interface TcpClientListener {
        public void onConnected();
        public void onReceived(int bodyLen, byte[] buff);
        public void onSend(int bodyLen, byte[] buff);
        public void onDisconnected();
        public void onConnectFail();
    }
}

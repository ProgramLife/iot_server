package com.xxx.iot.tcp;

import com.xxx.iot.common.BaseThred;
import com.xxx.iot.common.observer.EventSubject;
import com.xxx.iot.common.observer.EventType;
import com.xxx.iot.common.observer.ThreadType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * tcp 服务
 * Created by chen on 2018/12/18
 */
@Component
public class TcpServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile boolean runFlag;
    private TcpThread tcpThread;

    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;

    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpSocketAddress;

    private Channel channel;

    /**
     * 启动服务
     *
     * @throws Exception
     */
    public void startTcp() {
        if (runFlag) {
            return;
        }

        logger.info("tcp start listen port:" + tcpSocketAddress);
        EventSubject.getInstance().notifyObserver(EventType.ON_START, ThreadType.CHILD, tcpSocketAddress + "  tcp服务开启");
        try {
            runFlag = true;
            channel = serverBootstrap
                    .bind(tcpSocketAddress)
                    .sync()
                    .channel();
            channel.closeFuture()
                    .sync()
                    .channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭连接
     *
     * @throws Exception
     */
    @PreDestroy
    public void close() {
        EventSubject.getInstance().notifyObserver(EventType.ON_CLOSE, ThreadType.CHILD, tcpSocketAddress + "  tcp服务关闭");
        if (channel != null) {
            channel.close();
            runFlag = false;
        }
    }


    public void startTcpServer() {
        if (tcpThread != null) {
            tcpThread.stopThread();
            tcpThread = null;
        }
        tcpThread = new TcpThread();
        tcpThread.startThread();

    }

    public void stopTcpServer() {
        close();
        if (tcpThread != null) {
            tcpThread.stopThread();
        }
        tcpThread = null;
        runFlag = false;
    }

    private class TcpThread extends BaseThred {
        @Override
        public void run() {
            super.run();
            startTcp();
        }
    }
}

package com.xxx.iot.tcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 读取配置参数
 * Created by chen on 2018/12/19
 */
@ConfigurationProperties(prefix = "tcpserver")
public class TcpServerProperties {

    private String address;
    private int port;
    private int bossThreadCount;
    private int workerThreadCount;
    private boolean keepAlive;
    private int soBacklog;
    private int heartTime;
    private int timeout;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBossThreadCount() {
        return bossThreadCount;
    }

    public void setBossThreadCount(int bossThreadCount) {
        this.bossThreadCount = bossThreadCount;
    }

    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    public void setWorkerThreadCount(int workerThreadCount) {
        this.workerThreadCount = workerThreadCount;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
    }

    public int getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

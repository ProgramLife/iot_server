package com.xxx.iot.tcp.config;

import com.xxx.iot.tcp.TcpChannelInit;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Created by chen on 2018/12/19
 * tcp server 设置
 */
@Component
@Configuration
@EnableConfigurationProperties(TcpServerProperties.class)
public class TcpServerConfig {

    private final String address;
    private final int port;
    private final boolean keepalive;
    private final int bossThreadCount;
    private final int workerThreadCount;
    private final int soBacklog;

    public TcpServerConfig(TcpServerProperties tcpServerProperties) {
        this.address = tcpServerProperties.getAddress();
        this.port = tcpServerProperties.getPort();
        this.keepalive = tcpServerProperties.isKeepAlive();
        this.bossThreadCount = tcpServerProperties.getBossThreadCount();
        this.workerThreadCount = tcpServerProperties.getWorkerThreadCount();
        this.soBacklog = tcpServerProperties.getSoBacklog();
    }

    @Autowired
    @Qualifier("tcpChannelInit")
    private TcpChannelInit tcpChannelInit;

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpSocketAddress() {
        return new InetSocketAddress(address, port);
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossThreadCount);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerThreadCount);
    }

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(tcpChannelInit)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, soBacklog);

        return b;
    }

}

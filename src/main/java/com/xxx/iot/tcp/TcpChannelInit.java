package com.xxx.iot.tcp;

import com.xxx.iot.tcp.codec.MsgDecoder;
import com.xxx.iot.tcp.codec.MsgEncoder;
import com.xxx.iot.tcp.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 消息解码
 * Created by chen on 2018/12/19
 */
@Component
@Qualifier("tcpChannelInit")
public class TcpChannelInit extends ChannelInitializer<SocketChannel> {

    @Autowired
    @Qualifier("tcpServerHandler")
    private ServerHandler serverHandler;

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MsgDecoder());
        pipeline.addLast(new MsgEncoder());
        pipeline.addLast(serverHandler);
    }
}

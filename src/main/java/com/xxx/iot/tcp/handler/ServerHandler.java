package com.xxx.iot.tcp.handler;

import com.xxx.iot.tcp.codec.MsgProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Author: cdp
 * @Date: 2019/5/15 13:19
 * @Version 1.0
 */
@Component
@Qualifier("tcpServerHandler")
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MsgProtocol msgProtocol = new MsgProtocol();
        msgProtocol.setCc((short) 300);
        msgProtocol.setSessionId(1234);
        msgProtocol.setFlags((byte)1);
        msgProtocol.setVersion((byte)1);
        String body = "hello netty client";
        byte[] bodyArr = body.getBytes();
        msgProtocol.setBody(bodyArr);
        msgProtocol.setMsgBodyLen(bodyArr.length);
        ctx.writeAndFlush(msgProtocol);
        logger.info("连接成功" + bodyArr.length);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        MsgProtocol msgProtocol = new MsgProtocol();
        msgProtocol.setCc((short) 16);
        msgProtocol.setSessionId(1234);
        msgProtocol.setFlags((byte)1);
        msgProtocol.setVersion((byte)1);
        String body = "hello netty client";
        byte[] bodyArr = body.getBytes();
        msgProtocol.setBody(bodyArr);
        msgProtocol.setMsgBodyLen(bodyArr.length);
        ctx.writeAndFlush(msgProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

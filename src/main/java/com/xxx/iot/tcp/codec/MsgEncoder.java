package com.xxx.iot.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: cdp
 * @Date: 2019/5/15 13:29
 * @Version 1.0
 */
public class MsgEncoder extends MessageToByteEncoder<MsgProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgProtocol msgProtocol, ByteBuf out) throws Exception {

        if (msgProtocol.getVersion() == MsgConsts.HERT_VERSION) {
            out.writeByte(MsgConsts.HEART_MSG);
        } else {
            out.writeByte(msgProtocol.getHeadData());
            out.writeInt(msgProtocol.getMsgBodyLen());
            out.writeByte(msgProtocol.getVersion());
            out.writeShort(msgProtocol.getFlags());
            out.writeByte(msgProtocol.getCc());
            out.writeInt(msgProtocol.getSessionId());
            if (msgProtocol.getMsgBodyLen() > 0) {
                out.writeBytes(msgProtocol.getBody());
            }
        }

    }
}

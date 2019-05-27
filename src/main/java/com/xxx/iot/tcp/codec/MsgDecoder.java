package com.xxx.iot.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author: cdp
 * @Date: 2019/5/15 13:29
 * @Version 1.0
 * head(1) + msgBodyLen(4) + version(1) + flags(1) + cc(2) + sessionId(4) + body(n)
 */
public class MsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        decodeMsg(in, out);
    }

    private void decodeMsg(ByteBuf in, List<Object> out) {
        if (in.readableBytes() >= MsgConsts.HEADER_LEN) {
            if (in.readableBytes() > MsgConsts.MAX_BODY_LEN) {
                in.skipBytes(in.readableBytes());
            }
            // 记录当前读取位置（netty 特殊的buff）
            in.markReaderIndex();
            MsgProtocol msgProtocol = decodeFrame(in);
            if (msgProtocol != null) {
                out.add(msgProtocol);
            } else {
                //2.读取到不完整的frame,恢复到最近一次正常读取的位置,便于下次读取
                in.resetReaderIndex();
            }
        }
    }

    private MsgProtocol decodeFrame(ByteBuf in) {
        int readableBytes = in.readableBytes();
        int bodyLen = in.readInt();
        if (readableBytes < (bodyLen + MsgConsts.HEADER_LEN)) {
            return null;
        }
        MsgProtocol msgProtocol = new MsgProtocol();
        byte version = in.readByte();
        byte flags = in.readByte();
        short cc = in.readShort();
        int sessionId = in.readInt();
        byte[] body = new byte[bodyLen];
        if (bodyLen > 0) {
           in.readBytes(body);
        }
        msgProtocol.setMsgBodyLen(bodyLen);
        msgProtocol.setVersion(version);
        msgProtocol.setFlags(flags);
        msgProtocol.setCc(cc);
        msgProtocol.setSessionId(sessionId);
        msgProtocol.setBody(body);

        return msgProtocol;
    }
}

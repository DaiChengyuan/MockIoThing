package com.dcy.mockiothing.platform.core.transport.nettychannel;

import com.dcy.mockiothing.sdk.handler.DeviceMessage;
import com.dcy.mockiothing.sdk.handler.DeviceMessageEncoder;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.utils.ByteQueue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Qualifier("nettyMessageEncoder")
@Scope("prototype")
public class NettyMessageEncoder extends MessageToByteEncoder<DeviceMessage> {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, DeviceMessage msg, ByteBuf out) throws Exception {
        DeviceMessageEncoder deviceMessageEncoder = getDeviceMessageEncoder(ctx);
        if (deviceMessageEncoder != null) {
            ByteQueue byteQueue = deviceMessageEncoder.encode(msg);
            out.writeBytes(byteQueue.peekAll());
            log.info("send message: " + msg.toString());
        }
    }

    private DeviceMessageEncoder getDeviceMessageEncoder(ChannelHandlerContext ctx) {
        TransportAgent transportAgent;
        if (ctx.channel().parent() != null) {
            transportAgent = ctx.channel().parent().attr(NettyChannelAttribute.NETTY_CHANNEL_KEY).get();
        } else {
            transportAgent = ctx.channel().attr(NettyChannelAttribute.NETTY_CHANNEL_KEY).get();
        }
        return transportAgent.getDeviceMessageEncoder();
    }
}

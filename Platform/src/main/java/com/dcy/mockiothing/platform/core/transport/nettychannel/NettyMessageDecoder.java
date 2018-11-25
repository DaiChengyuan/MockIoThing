package com.dcy.mockiothing.platform.core.transport.nettychannel;

import com.dcy.mockiothing.sdk.handler.DeviceMessage;
import com.dcy.mockiothing.sdk.handler.DeviceMessageDecoder;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.utils.ByteQueue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("nettyMessageDecoder")
@Scope("prototype")
public class NettyMessageDecoder extends ByteToMessageDecoder {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        DeviceMessage deviceMessage = decode(ctx, in);
        if (deviceMessage != null) {
            out.add(deviceMessage);
        }
    }

    private DeviceMessage decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        if (bytes.length <= 0) {
            return null;
        }
        in.getBytes(0, bytes);
        ByteQueue byteQueue = new ByteQueue(bytes);
        int originalSize = byteQueue.size();

        DeviceMessageDecoder deviceMessageDecoder = getDeviceMessageDecoder(ctx);
        if (deviceMessageDecoder == null) {
            return null;
        }
        DeviceMessage deviceMessage = deviceMessageDecoder.decode(byteQueue);
        int remainingSize = byteQueue.size();

        if (deviceMessage != null) {
            log.info("recv message: " + deviceMessage.toString());
            in.readBytes(originalSize - remainingSize);
        }

        return deviceMessage;
    }

    private DeviceMessageDecoder getDeviceMessageDecoder(ChannelHandlerContext ctx) {
        TransportAgent transportAgent;
        if (ctx.channel().parent() != null) {
            transportAgent = ctx.channel().parent().attr(NettyChannelAttribute.NETTY_CHANNEL_KEY).get();
        } else {
            transportAgent = ctx.channel().attr(NettyChannelAttribute.NETTY_CHANNEL_KEY).get();
        }
        return transportAgent.getDeviceMessageDecoder();
    }
}

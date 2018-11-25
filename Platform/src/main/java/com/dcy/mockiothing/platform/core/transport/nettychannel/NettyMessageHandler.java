package com.dcy.mockiothing.platform.core.transport.nettychannel;

import com.dcy.mockiothing.sdk.handler.DeviceMessage;
import com.dcy.mockiothing.sdk.handler.DeviceMessageHandler;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("nettyMessageHandler")
@ChannelHandler.Sharable
public class NettyMessageHandler extends ChannelInboundHandlerAdapter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        DeviceMessage deviceMessage = new DeviceMessage() {};
        deviceMessage.setDeviceMessageType(DeviceMessage.TYPE.MSG_ON_OPEN);
        handleMsg(ctx, deviceMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DeviceMessage deviceMessage = (DeviceMessage) msg;
        deviceMessage.setDeviceMessageType(DeviceMessage.TYPE.MSG_ON_READ);
        handleMsg(ctx, deviceMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        DeviceMessage deviceMessage = new DeviceMessage() {};
        deviceMessage.setDeviceMessageType(DeviceMessage.TYPE.MSG_ON_SHUT);
        handleMsg(ctx, deviceMessage);
    }

    private void handleMsg(ChannelHandlerContext ctx, DeviceMessage in) {
        DeviceMessageHandler deviceMessageHandler = getDeviceMessageHandler(ctx);
        if (deviceMessageHandler != null) {
            DeviceMessage out = deviceMessageHandler.handleMsg(in);
            if (out != null) {
                ctx.writeAndFlush(out);
            }
        }
    }

    private DeviceMessageHandler getDeviceMessageHandler(ChannelHandlerContext ctx) {
        TransportAgent transportAgent;
        if (ctx.channel().parent() != null) {
            transportAgent = ctx.channel().parent().attr(NettyChannelAttribute.NETTY_CHANNEL_KEY).get();
        } else {
            transportAgent = ctx.channel().attr(NettyChannelAttribute.NETTY_CHANNEL_KEY).get();
        }
        return transportAgent.getDeviceMessageHandler();
    }
}

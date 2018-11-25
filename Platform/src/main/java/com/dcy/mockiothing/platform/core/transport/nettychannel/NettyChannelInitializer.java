package com.dcy.mockiothing.platform.core.transport.nettychannel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("nettyChannelInitializer")
public class NettyChannelInitializer extends ChannelInitializer {

    @Autowired
    @Qualifier("nettyMessageHandler")
    private ChannelInboundHandlerAdapter nettyMessageHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new NettyMessageDecoder());
        pipeline.addLast(new NettyMessageEncoder());
        pipeline.addLast(nettyMessageHandler);
    }
}

package com.dcy.mockiothing.platform.core.transport.nettychannel;

import com.dcy.mockiothing.sdk.transport.TransportAgent;
import io.netty.util.AttributeKey;

public class NettyChannelAttribute {

    public static final AttributeKey<TransportAgent> NETTY_CHANNEL_KEY = new AttributeKey<>("default");

}

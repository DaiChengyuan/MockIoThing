package com.dcy.mockiothing.sdk.handler;

import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.utils.ByteQueue;

public abstract class DeviceMessageEncoder {
    private final TransportAgent parent;

    protected DeviceMessageEncoder(TransportAgent parent) {
        this.parent = parent;
    }

    protected TransportAgent getParentTransportAgent() {
        return parent;
    }

    public abstract ByteQueue encode(DeviceMessage msg);
}

package com.dcy.mockiothing.sdk.handler;

import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.utils.ByteQueue;

public abstract class DeviceMessageDecoder {
    private final TransportAgent parent;

    protected DeviceMessageDecoder(TransportAgent parent) {
        this.parent = parent;
    }

    protected TransportAgent getParentTransportAgent() {
        return parent;
    }

    public abstract DeviceMessage decode(ByteQueue in);
}

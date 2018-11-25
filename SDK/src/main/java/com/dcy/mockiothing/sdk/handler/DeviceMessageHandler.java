package com.dcy.mockiothing.sdk.handler;

import com.dcy.mockiothing.sdk.transport.TransportAgent;

public abstract class DeviceMessageHandler {
    private final TransportAgent parent;

    protected DeviceMessageHandler(TransportAgent parent) {
        this.parent = parent;
    }

    protected TransportAgent getParentTransportAgent() {
        return parent;
    }

    public abstract DeviceMessage handleMsg(DeviceMessage deviceMessage);
}

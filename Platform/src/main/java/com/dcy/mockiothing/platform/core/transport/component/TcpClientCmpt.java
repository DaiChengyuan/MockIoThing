package com.dcy.mockiothing.platform.core.transport.component;

import com.dcy.mockiothing.sdk.handler.DeviceMessage;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.transport.TransportComponent;

import java.util.Map;

public class TcpClientCmpt extends TransportComponent {
    protected TcpClientCmpt(TransportAgent parent) {
        super(parent);
    }

    @Override
    protected void addTransComponentParams() {

    }

    @Override
    protected void init(Map<String, String> transportDataPoints, TransportAgent.CallBack callBack) {

    }

    @Override
    protected void open() throws InterruptedException {

    }

    @Override
    protected void shut() throws InterruptedException {

    }

    @Override
    protected void send(DeviceMessage msg) throws InterruptedException {

    }

    @Override
    protected void recv() throws InterruptedException {

    }
}

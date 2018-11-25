package com.dcy.mockiothing.sdk.transport;

import com.dcy.mockiothing.sdk.DeviceModel;
import com.dcy.mockiothing.sdk.handler.DeviceMessage;
import com.dcy.mockiothing.sdk.handler.DeviceMessageDecoder;
import com.dcy.mockiothing.sdk.handler.DeviceMessageEncoder;
import com.dcy.mockiothing.sdk.handler.DeviceMessageHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class TransportAgent {
    private final DeviceModel parent;
    private final String transportAgentName;
    private final TransportParams.TYPE transportAgentType;
    private final DeviceMessageDecoder deviceMessageDecoder;
    private final DeviceMessageEncoder deviceMessageEncoder;
    private final DeviceMessageHandler deviceMessageHandler;
    private TransportComponent transportComponent;

    public TransportAgent(String transportAgentName, TransportParams.TYPE transportAgentType, DeviceModel parent) {
        this.parent = parent;
        this.transportAgentName = transportAgentName;
        this.transportAgentType = transportAgentType;
        this.deviceMessageDecoder = addDeviceMessageDecoder();
        this.deviceMessageEncoder = addDeviceMessageEncoder();
        this.deviceMessageHandler = addDeviceMessageHandler();
    }

    public DeviceModel getParentDeviceModel() {
        return parent;
    }

    public String getTransportAgentName() {
        return transportAgentName;
    }

    public TransportParams.TYPE getTransportAgentType() {
        return transportAgentType;
    }

    public DeviceMessageDecoder getDeviceMessageDecoder() {
        return deviceMessageDecoder;
    }

    public DeviceMessageEncoder getDeviceMessageEncoder() {
        return deviceMessageEncoder;
    }

    public DeviceMessageHandler getDeviceMessageHandler() {
        return deviceMessageHandler;
    }

    public TransportComponent getTransportComponent() {
        return transportComponent;
    }

    public void setTransportComponent(TransportComponent transportComponent) {
        this.transportComponent = transportComponent;
        initTransportDataPoints();
    }

    public Map<String, String> getTransportDataPoints() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : transportComponent.getTransComponentParams().entrySet()) {
            map.put(entry.getKey(), parent.getDeviceDataPoints().getDeviceDataPoint(entry.getKey()));
        }
        return map;
    }

    public void setTransportDataPoint(String transportParamName, String transportParamValue) {
        String transportDataPoint = parent.getDeviceDataPoints().getDeviceDataPoint(getTransportAgentName() + transportParamName);
        if (transportDataPoint == null || "".equals(transportDataPoint)) {
            transportComponent.setTransComponentParam(transportParamName, transportParamValue);
            syncTransportDataPoints();
        }
    }

    public void updTransportDataPoint(String transportParamName, String transportParamValue) {
        transportComponent.setTransComponentParam(transportParamName, transportParamValue);
        syncTransportDataPoints();
    }

    private void initTransportDataPoints() {
        if (transportComponent != null) {
            for (Map.Entry<String, String> entry : transportComponent.getTransComponentParams().entrySet()) {
                parent.getDeviceDataPoints().addDeviceDataPoint(entry.getKey(), entry.getValue());
            }
        }
    }

    private void syncTransportDataPoints() {
        if (transportComponent != null) {
            for (Map.Entry<String, String> entry : transportComponent.getTransComponentParams().entrySet()) {
                parent.getDeviceDataPoints().updDeviceDataPoint(entry.getKey(), entry.getValue());
            }
        }
    }

    public interface CallBack {
        void execute();
    }

    public synchronized void onCreate() throws InterruptedException {
        try {
            transportComponent.getLock().lock();
            transportComponent.setParentTransportAgent(this);
            transportComponent.init(getTransportDataPoints(), this::syncTransportDataPoints);
            transportComponent.open();
        } finally {
            transportComponent.getLock().unlock();
        }
    }

    public synchronized void onUpdate() throws InterruptedException {
        try {
            transportComponent.getLock().lock();
            transportComponent.setParentTransportAgent(this);
            transportComponent.shut();
            transportComponent.init(getTransportDataPoints(), this::syncTransportDataPoints);
            transportComponent.open();
        } finally {
            transportComponent.getLock().unlock();
        }
    }

    public synchronized void onDelete() throws InterruptedException {
        try {
            transportComponent.getLock().lock();
            transportComponent.setParentTransportAgent(this);
            transportComponent.shut();
        } finally {
            transportComponent.getLock().unlock();
            transportComponent = null;
        }
    }

    public synchronized void sendData(DeviceMessage deviceMessage) throws InterruptedException {
        try {
            transportComponent.getLock().lock();
            transportComponent.setParentTransportAgent(this);
            transportComponent.send(deviceMessage);
        } finally {
            transportComponent.getLock().unlock();
        }
    }

    protected abstract DeviceMessageDecoder addDeviceMessageDecoder();

    protected abstract DeviceMessageEncoder addDeviceMessageEncoder();

    protected abstract DeviceMessageHandler addDeviceMessageHandler();
}

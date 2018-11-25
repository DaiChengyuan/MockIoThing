package com.dcy.mockiothing.sdk;


import com.dcy.mockiothing.sdk.actor.DeviceActionMachine;
import com.dcy.mockiothing.sdk.actor.DeviceEvent;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.transport.TransportParams;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class DeviceModel {
    private Logger log = Logger.getLogger(this.getClass());

    private final String deviceModelName;
    private final DeviceDataPoints deviceDataPoints;
    private final DeviceActionMachine deviceActionMachine;
    private final Map<String, TransportAgent> transportAgentMap = new HashMap<>();

    public DeviceModel() {
        this.deviceModelName = setDeviceModelName();
        this.deviceDataPoints = setDeviceDataPoints();
        this.deviceActionMachine = setDeviceActionMachine();
        this.setTransportAgents();
    }

    public String getDeviceModelName() {
        return deviceModelName;
    }

    public DeviceDataPoints getDeviceDataPoints() {
        return deviceDataPoints;
    }

    public DeviceActionMachine getDeviceActionMachine() {
        return deviceActionMachine;
    }

    public Map<String, TransportAgent> getTransportAgentMap() {
        return transportAgentMap;
    }

    public TransportAgent getTransportAgentByName(String transportAgentName) {
        return transportAgentMap.get(transportAgentName);
    }

    public void onDeviceCreated(Map<String, String> deviceDataPoints) {
        if (deviceDataPoints != null) {
            this.deviceDataPoints.updDeviceDataPoints(deviceDataPoints);
        }
        if (deviceActionMachine != null) {
            deviceActionMachine.onReceiveDeviceEvent(new DeviceEvent.DeviceCreatedEvent(this));
        }
        log.info(this.toString() + " is created");
    }

    public void onDeviceUpdated(Map<String, String> deviceDataPoints) {
        if (deviceDataPoints != null) {
            this.deviceDataPoints.updDeviceDataPoints(deviceDataPoints);
        }
        if (deviceActionMachine != null) {
            deviceActionMachine.onReceiveDeviceEvent(new DeviceEvent.DeviceUpdatedEvent(this));
        }
        log.info(this.toString() + " is updated");
    }

    public void onDeviceDeleted() {
        if (deviceActionMachine != null) {
            deviceActionMachine.onReceiveDeviceEvent(new DeviceEvent.DeviceDeletedEvent(this));
        }
        for (Map.Entry<String, TransportAgent> entry : transportAgentMap.entrySet()) {
            try {
                TransportAgent transportAgent = entry.getValue();
                transportAgent.onDelete();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info(this.toString() + " is deleted");
    }

    protected abstract String setDeviceModelName();

    protected abstract DeviceDataPoints setDeviceDataPoints();

    protected abstract DeviceActionMachine setDeviceActionMachine();

    protected abstract void setTransportAgents();

    protected void addTransportAgent(TransportAgent transportAgent) {
        this.transportAgentMap.put(transportAgent.getTransportAgentName(), transportAgent);
    }

    protected void updTransportAgent() {}
}

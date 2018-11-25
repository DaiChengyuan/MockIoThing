package com.dcy.mockiothing.sdk.handler;

public abstract class DeviceMessage {
    public enum TYPE {
        MSG_ON_OPEN,
        MSG_ON_READ,
        MSG_ON_SHUT
    }
    private TYPE deviceMessageType;

    private String deviceIdentification;

    public TYPE getDeviceMessageType() {
        return deviceMessageType;
    }

    public void setDeviceMessageType(TYPE deviceMessageType) {
        this.deviceMessageType = deviceMessageType;
    }

    public String getDeviceIdentification() {
        return deviceIdentification;
    }

    public void setDeviceIdentification(String transportAgentName, String deviceIdentification) {
        this.deviceIdentification = transportAgentName + "@" + deviceIdentification;
    }
}

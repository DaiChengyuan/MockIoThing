package com.dcy.mockiothing.sdk;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_NAME;
import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_STATUS;
import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_UUID;

public abstract class DeviceDataPoints {
    private final Map<String, String> deviceDataPointMap = new LinkedHashMap<>();

    private String deviceIdentificationPoint;

    protected DeviceDataPoints() {
        addCommonDataPoints();
        addCustomDataPoints();
    }

    private void addCommonDataPoints() {
        this.deviceDataPointMap.put(DEVICE_UUID, null);
        this.deviceDataPointMap.put(DEVICE_NAME, null);
        this.deviceDataPointMap.put(DEVICE_STATUS, null);
    }

    private void addCustomDataPoints() {
        addDeviceDataPoints();
    }

    protected abstract void addDeviceDataPoints();

    public void addDeviceDataPoint(String name, String value) {
        this.deviceDataPointMap.put(name, value);
    }

    public void updDeviceDataPoint(String name, String value) {
        for (String deviceDataPoint: deviceDataPointMap.keySet()) {
            if (deviceDataPoint.equals(name)) {
                deviceDataPointMap.put(deviceDataPoint, value);
            }
        }
    }

    public void updDeviceDataPoints(Map<String, String> deviceDataPoints) {
        for (Map.Entry<String, String> entry : deviceDataPoints.entrySet()) {
            updDeviceDataPoint(entry.getKey(), entry.getValue());
        }
    }

    public void delDeviceDataPoint() {}

    public Map<String, String> getDeviceDataPointMap() {
        return deviceDataPointMap;
    }

    public String getDeviceDataPoint(String name) {
        return deviceDataPointMap.get(name);
    }

    public void setDeviceIdentificationPoint(String deviceIdentificationPoint) {
        this.deviceIdentificationPoint = deviceIdentificationPoint;
    }

    public String getDeviceIdentificationPoint() {
        return deviceDataPointMap.get(deviceIdentificationPoint);
    }

    @Override
    public String toString() {
        return deviceDataPointMap.toString();
    }
}

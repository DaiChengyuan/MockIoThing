package com.dcy.mockiothing.platform.core;

import com.dcy.mockiothing.platform.common.util.SpringUtil;
import com.dcy.mockiothing.sdk.DeviceModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("mockFactory")
public class MockFactory {
    private Map<String, DeviceModel> deviceModelMap = new HashMap<>();

    public void setDeviceModelMap(Map<String, DeviceModel> deviceModelMap) {
        this.deviceModelMap = deviceModelMap;
    }

    public Map<String, DeviceModel> getDeviceModelMap() {
        return deviceModelMap;
    }

    public DeviceModel getDeviceModelByName(String deviceModelName) {
        return (DeviceModel) SpringUtil.getBean(deviceModelName);
    }
}

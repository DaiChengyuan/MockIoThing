package com.dcy.mockiothing.platform.core.deviceshadow;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("prototype")
public class DeviceShadowList {
    private Map<String, DeviceShadow> deviceShadowMap = new ConcurrentHashMap<>();

    void insertDeviceShadowById(String id, DeviceShadow deviceShadow) {
        deviceShadowMap.put(id, deviceShadow);
    }

    DeviceShadow selectDeviceShadowById(String id) {
        return deviceShadowMap.get(id);
    }

    void deleteDeviceShadowById(String id) {
        deviceShadowMap.remove(id);
    }
}

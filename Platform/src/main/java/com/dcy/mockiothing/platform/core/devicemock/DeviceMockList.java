package com.dcy.mockiothing.platform.core.devicemock;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("prototype")
public class DeviceMockList {
    private Map<String, DeviceMock> deviceMockMap = new ConcurrentHashMap<>();

    Map<String, DeviceMock> getDeviceMockMap() {
        return deviceMockMap;
    }

    void insertDeviceMockById(String id, DeviceMock deviceMock) {
        deviceMockMap.put(id, deviceMock);
    }

    DeviceMock selectDeviceMockById(String id) {
        return deviceMockMap.get(id);
    }

    void deleteDeviceMockById(String id) {
        deviceMockMap.remove(id);
    }
}

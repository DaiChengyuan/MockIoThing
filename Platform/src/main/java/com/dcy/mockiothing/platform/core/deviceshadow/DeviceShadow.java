package com.dcy.mockiothing.platform.core.deviceshadow;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class DeviceShadow {
    private Map<String, String> shadowDataPoints = new HashMap<>();

    public Map<String, String> getShadowDataPoints() {
        return shadowDataPoints;
    }

    void update(Map<String, String> deviceDataPoints) {
        for (String key : deviceDataPoints.keySet()) {
            shadowDataPoints.put(key, deviceDataPoints.get(key));
        }
    }

    void delete() {
        shadowDataPoints = null;
    }
}

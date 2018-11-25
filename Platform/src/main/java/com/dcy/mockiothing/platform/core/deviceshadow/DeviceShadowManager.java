package com.dcy.mockiothing.platform.core.deviceshadow;

import com.dcy.mockiothing.platform.common.util.SpringUtil;
import com.dcy.mockiothing.sdk.DeviceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_UUID;

@Component
@Qualifier("deviceShadowManager")
public class DeviceShadowManager {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, DeviceShadowList> deviceShadowListMap = new HashMap<>();

    public void init(List<DeviceModel> deviceModelList) {
        for (DeviceModel deviceModel : deviceModelList) {
            DeviceShadowList deviceShadowList = SpringUtil.getBeanByType(DeviceShadowList.class);
            deviceShadowListMap.put(deviceModel.getDeviceModelName(), deviceShadowList);
        }
    }

    public void createDeviceShadow(String deviceModelName, Map<String, String> deviceDataPoints) {
        DeviceShadow deviceShadow = SpringUtil.getBeanByType(DeviceShadow.class);
        deviceShadow.update(deviceDataPoints);
        String id = deviceDataPoints.get(DEVICE_UUID);
        deviceShadowListMap.get(deviceModelName).insertDeviceShadowById(id, deviceShadow);
        log.info("shadow create: {} | {} | {}", deviceModelName, id, deviceDataPoints);
    }

    public void updateDeviceShadow(String deviceModelName, Map<String, String> deviceDataPoints, String id) {
        DeviceShadow deviceShadow = deviceShadowListMap.get(deviceModelName).selectDeviceShadowById(id);
        deviceShadow.update(deviceDataPoints);
        log.info("shadow update: {} | {} | {}", deviceModelName, id, deviceDataPoints);
    }

    public void deleteDeviceShadow(String deviceModelName, String id) {
        DeviceShadowList deviceShadowList = deviceShadowListMap.get(deviceModelName);
        DeviceShadow deviceShadow = deviceShadowList.selectDeviceShadowById(id);
        if (deviceShadow == null)
            return;
        deviceShadow.delete();
        deviceShadowList.deleteDeviceShadowById(id);
        deviceShadow = null;
        log.info("shadow delete: {} | {}", deviceModelName, id);
    }

    public DeviceShadowList selectDeviceShadowList(String deviceModelName) {
        return deviceShadowListMap.get(deviceModelName);
    }

    public DeviceShadow selectDeviceShadow(String deviceModelName, String id) {
        return deviceShadowListMap.get(deviceModelName).selectDeviceShadowById(id);
    }

    public Map<String, String> lookupChangedDataPoints(DeviceShadow deviceShadow, Map<String, String> deviceDataPoints) {
        Map<String, String> changedDataPoints = new HashMap<>();
        Map<String, String> shadowDataPoints = deviceShadow.getShadowDataPoints();
        for (Map.Entry<String, String> entry : deviceDataPoints.entrySet()) {
            String dataPointName = entry.getKey();
            String dataPointValue = entry.getValue();
            if (!shadowDataPoints.get(dataPointName).equals(dataPointValue)) {
                changedDataPoints.put(dataPointName, dataPointValue);
            }
        }
        return changedDataPoints;
    }
}

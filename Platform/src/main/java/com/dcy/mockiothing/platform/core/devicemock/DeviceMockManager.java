package com.dcy.mockiothing.platform.core.devicemock;

import com.dcy.mockiothing.platform.common.util.SpringUtil;
import com.dcy.mockiothing.sdk.DeviceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_UUID;

@Component
@Qualifier("deviceMockManager")
public class DeviceMockManager {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, DeviceMockList> deviceMockListMap = new HashMap<>();

    public void init(List<DeviceModel> deviceModelList) {
        for (DeviceModel deviceModel : deviceModelList) {
            DeviceMockList deviceMockList = SpringUtil.getBeanByType(DeviceMockList.class);
            deviceMockListMap.put(deviceModel.getDeviceModelName(), deviceMockList);
        }
    }

    public void createDeviceMock(String deviceModelName, Map<String, String> deviceDataPoints) {
        DeviceMock deviceMock = SpringUtil.getBeanByType(DeviceMock.class);
        deviceMock.create(deviceModelName, deviceDataPoints);
        String id = deviceDataPoints.get(DEVICE_UUID);
        deviceMockListMap.get(deviceModelName).insertDeviceMockById(id, deviceMock);
        log.info("mock   create: {} | {} | {}", deviceModelName, id, deviceDataPoints);
    }

    public void updateDeviceMock(String deviceModelName, Map<String, String> deviceDataPoints, String id) {
        DeviceMock deviceMock = deviceMockListMap.get(deviceModelName).selectDeviceMockById(id);
        deviceMock.update(deviceDataPoints);
        log.info("mock   update: {} | {} | {}", deviceModelName, id, deviceDataPoints);
    }

    public void deleteDeviceMock(String deviceModelName, String id) {
        DeviceMockList deviceMockList = deviceMockListMap.get(deviceModelName);
        DeviceMock deviceMock = deviceMockList.selectDeviceMockById(id);
        if (deviceMock == null)
            return;
        deviceMock.delete();
        deviceMockList.deleteDeviceMockById(id);
        deviceMock = null;
        log.info("mock   delete: {} | {}", deviceModelName, id);
    }

    public List<Map<String, String>> selectDeviceDataList(String deviceModleName) {
        List<Map<String, String>> deviceDataPointsListFromMock = new ArrayList<>();
        DeviceMockList deviceMockList = deviceMockListMap.get(deviceModleName);
        for (Map.Entry<String, DeviceMock> entry : deviceMockList.getDeviceMockMap().entrySet()) {
            DeviceMock deviceMock = entry.getValue();
            Map<String, String> deviceDataPoints = deviceMock.selectDeviceDataPoints();
            deviceDataPointsListFromMock.add(deviceDataPoints);
        }
        return deviceDataPointsListFromMock;
    }
}

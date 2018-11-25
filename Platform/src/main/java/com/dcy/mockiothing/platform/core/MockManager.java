package com.dcy.mockiothing.platform.core;

import com.dcy.mockiothing.platform.common.util.SpringUtil;
import com.dcy.mockiothing.platform.core.devicemock.DeviceMockManager;
import com.dcy.mockiothing.platform.core.deviceshadow.DeviceShadow;
import com.dcy.mockiothing.platform.core.deviceshadow.DeviceShadowManager;
import com.dcy.mockiothing.platform.dao.DeviceMockDao;
import com.dcy.mockiothing.sdk.DeviceModel;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.transport.TransportComponent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.dcy.mockiothing.platform.core.transport.component.TransCmptFactory.newTransportComponent;
import static com.dcy.mockiothing.sdk.utils.Contants.*;

@Component
public class MockManager {
    private List<DeviceModel> deviceModelList = new ArrayList<>();

    @PostConstruct
    public void init() {
        initDeviceModelList();
        initDeviceMockManager();
        initDeviceShadowManager();
        initMockManagementTask();
    }

    private void initDeviceModelList() {
        MockFactory mockFactory = (MockFactory) SpringUtil.getBean("mockFactory");
        Map<String, DeviceModel> deviceModelMap = mockFactory.getDeviceModelMap();
        for (Map.Entry<String, DeviceModel> entry : deviceModelMap.entrySet()) {
            DeviceModel deviceModel = entry.getValue();
            registDeviceModel(deviceModel);
        }
    }

    private void initDeviceMockManager() {
        DeviceMockManager deviceMockManager = (DeviceMockManager) SpringUtil.getBean("deviceMockManager");
        deviceMockManager.init(deviceModelList);
    }

    private void initDeviceShadowManager() {
        DeviceShadowManager deviceShadowManager = (DeviceShadowManager) SpringUtil.getBean("deviceShadowManager");
        deviceShadowManager.init(deviceModelList);
    }

    private void initMockManagementTask() {
        for (DeviceModel deviceModel : deviceModelList) {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(new MockManagementTask(deviceModel), 0, 3, TimeUnit.SECONDS);
        }
    }

    private void registDeviceModel(DeviceModel deviceModel) {
        Map<String, TransportAgent> transportAgentMap = deviceModel.getTransportAgentMap();
        for (Map.Entry<String, TransportAgent> entry : transportAgentMap.entrySet()) {
            TransportAgent transportAgent = entry.getValue();
            TransportComponent transportComponent = newTransportComponent(transportAgent, null);
            transportAgent.setTransportComponent(transportComponent);
        }
        deviceModelList.add(deviceModel);
    }

    class MockManagementTask implements Runnable {
        private String deviceModelName;
        private Map<String, String> deviceDataPoints;
        private DeviceMockManager deviceMockManager = SpringUtil.getBeanByType(DeviceMockManager.class);
        private DeviceShadowManager deviceShadowManager = SpringUtil.getBeanByType(DeviceShadowManager.class);
        private DeviceMockDao deviceMockDao = SpringUtil.getBeanByType(DeviceMockDao.class);
        private Date lastUpdatetime;

        MockManagementTask(DeviceModel deviceModel) {
            this.deviceModelName = deviceModel.getDeviceModelName();
            this.deviceDataPoints = deviceModel.getDeviceDataPoints().getDeviceDataPointMap();
            initDeviceMockTable();
        }

        @Override
        public void run() {
            try {
                Date thisUpdateTime = new Date();

                List<Map<String, String>> deviceDataPointsListFromDB =
                        deviceMockDao.selectDeviceTable(deviceModelName, deviceDataPoints, lastUpdatetime);

                for (Map<String, String> deviceDataPointsFromDB : deviceDataPointsListFromDB) {
                    handleDeviceDataPointsFromDB(deviceModelName, deviceDataPointsFromDB);
                }

                Thread.sleep(3000);

                List<Map<String, String>> deviceDataPointsListFromMock =
                        deviceMockManager.selectDeviceDataList(deviceModelName);

                for (Map<String, String> deviceDataPointsFromMock : deviceDataPointsListFromMock) {
                    handleDeviceDataPointsFromMock(deviceModelName, deviceDataPointsFromMock);
                }

                lastUpdatetime = thisUpdateTime;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void initDeviceMockTable() {
            deviceMockDao.createDeviceTable(deviceModelName, deviceDataPoints);
        }

        private void handleDeviceDataPointsFromDB(String deviceModelName, Map<String, String> deviceDataPoints) {
            String id = deviceDataPoints.get(DEVICE_UUID);
            DeviceShadow deviceShadow = deviceShadowManager.selectDeviceShadow(deviceModelName, id);
            if (deviceShadow == null) {
                String status = deviceDataPoints.get(DEVICE_STATUS);
                if (status == null) {
                    return;
                }
                if (status.equals(DEVICE_STATUS_ON)) {
                    deviceMockManager.createDeviceMock(deviceModelName, deviceDataPoints);
                    deviceShadowManager.createDeviceShadow(deviceModelName, deviceDataPoints);
                }
            } else {
                Map<String, String> changedDataPoints;
                changedDataPoints = deviceShadowManager.lookupChangedDataPoints(deviceShadow, deviceDataPoints);
                if (changedDataPoints.isEmpty()) {
                    return;
                }
                String status = changedDataPoints.get(DEVICE_STATUS);
                if (status == null) {
                    deviceMockManager.updateDeviceMock(deviceModelName, changedDataPoints, id);
                    deviceShadowManager.updateDeviceShadow(deviceModelName, changedDataPoints, id);
                } else if (status.equals(DEVICE_STATUS_OFF)) {
                    deviceMockManager.deleteDeviceMock(deviceModelName, id);
                    deviceShadowManager.deleteDeviceShadow(deviceModelName, id);
                }
            }
        }

        private void handleDeviceDataPointsFromMock(String deviceModelName, Map<String, String> deviceDataPoints) {
            String id = deviceDataPoints.get(DEVICE_UUID);
            DeviceShadow deviceShadow = deviceShadowManager.selectDeviceShadow(deviceModelName, id);
            if (deviceShadow == null) {
                deviceMockDao.insertDeviceTable(deviceModelName, deviceDataPoints);
                deviceShadowManager.createDeviceShadow(deviceModelName, deviceDataPoints);
            } else {
                Map<String, String> changedDataPoints;
                changedDataPoints = deviceShadowManager.lookupChangedDataPoints(deviceShadow, deviceDataPoints);
                if (changedDataPoints.isEmpty()) {
                    return;
                }
                String status = changedDataPoints.get(DEVICE_STATUS);
                if (status == null) {
                    deviceMockDao.updateDeviceTable(deviceModelName, changedDataPoints, id);
                    deviceShadowManager.updateDeviceShadow(deviceModelName, changedDataPoints, id);
                } else if (status.equals(DEVICE_STATUS_OFF)) {
                    changedDataPoints.remove(DEVICE_STATUS);
                    deviceMockDao.updateDeviceTable(deviceModelName, changedDataPoints, id);
                    deviceMockManager.deleteDeviceMock(deviceModelName, id);
                    deviceShadowManager.deleteDeviceShadow(deviceModelName, id);
                }
            }
        }
    }
}

package com.dcy.mockiothing.platform.core.devicemock;

import com.dcy.mockiothing.platform.common.util.SpringUtil;
import com.dcy.mockiothing.platform.core.MockFactory;
import com.dcy.mockiothing.sdk.DeviceModel;
import com.dcy.mockiothing.sdk.actor.DeviceActionMachine;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.transport.TransportComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.dcy.mockiothing.platform.core.transport.component.TransCmptFactory.newTransportComponent;
import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_STATUS;
import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_STATUS_OFF;

@Component
@Scope("prototype")
public class DeviceMock {
    private DeviceModel deviceModel;

    void create(String deviceModelName, Map<String, String> deviceDataPoints) {
        MockFactory mockFactory = (MockFactory) SpringUtil.getBean("mockFactory");
        DeviceModel deviceModel = mockFactory.getDeviceModelByName(deviceModelName);
        initDeviceModel(deviceModel, deviceDataPoints);
        deviceModel.onDeviceCreated(deviceDataPoints);
    }

    void update(Map<String, String> deviceDataPoints) {
        for (Map.Entry<String, TransportAgent> entry : deviceModel.getTransportAgentMap().entrySet()) {
            Map<String, String> transportDataPoints = entry.getValue().getTransportDataPoints();
            if (ifTransportChanged(transportDataPoints, deviceDataPoints)) {
                deviceModel.getDeviceDataPoints().updDeviceDataPoint(DEVICE_STATUS, DEVICE_STATUS_OFF);
            }
        }
        deviceModel.onDeviceUpdated(deviceDataPoints);
    }

    void delete() {
        deviceModel.onDeviceDeleted();
        deviceModel = null;
    }

    Map<String, String> selectDeviceDataPoints() {
        return deviceModel.getDeviceDataPoints().getDeviceDataPointMap();
    }

    private boolean ifTransportChanged(Map<String, String> transportDataPoints, Map<String, String> deviceDataPoints) {
        for (Map.Entry<String, String> entry : deviceDataPoints.entrySet()) {
            if (transportDataPoints.get(entry.getKey()) != null) {
                return true;
            }
        }
        return false;
    }

    private void initDeviceModel(DeviceModel deviceModel, Map<String, String> deviceDataPoints) {
        this.deviceModel = deviceModel;
        initDeviceActionExecutor();
        initDeviceTransportComponent(deviceDataPoints);
    }

    private void initDeviceActionExecutor() {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) SpringUtil.getBean("deviceActionExecutor");
        DeviceActionMachine deviceActionMachine = deviceModel.getDeviceActionMachine();
        if (deviceActionMachine != null) {
            deviceActionMachine.setExecutor(executor);
        }
    }

    private void initDeviceTransportComponent(Map<String, String> deviceDataPoints) {
        Map<String, TransportAgent> transportAgentMap = deviceModel.getTransportAgentMap();
        for (Map.Entry<String, TransportAgent> entry : transportAgentMap.entrySet()) {
            TransportAgent transportAgent = entry.getValue();
            TransportComponent transportComponent = newTransportComponent(transportAgent, deviceDataPoints);
            transportAgent.setTransportComponent(transportComponent);
        }
    }
}

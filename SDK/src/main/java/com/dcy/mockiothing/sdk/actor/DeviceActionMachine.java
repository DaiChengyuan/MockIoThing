package com.dcy.mockiothing.sdk.actor;

import com.dcy.mockiothing.sdk.DeviceModel;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class DeviceActionMachine implements EventListener {
    private final DeviceModel parent;
    private final Map<String, DeviceAction> deviceActionMap = new HashMap<>();
    private ScheduledThreadPoolExecutor deviceActionExecutor;

    protected DeviceActionMachine(DeviceModel deviceModel) {
        this.parent = deviceModel;
    }

    public DeviceModel getParentDeviceModel() {
        return parent;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return deviceActionExecutor;
    }

    public void setExecutor(ScheduledThreadPoolExecutor executor) {
        this.deviceActionExecutor = executor;
    }

    protected void addDeviceActionByName(String actionName, DeviceAction deviceAction) {
        this.deviceActionMap.put(actionName, deviceAction);
    }

    protected DeviceAction getDeviceActionByName(String actionName) {
        return deviceActionMap.get(actionName);
    }

    protected void executeDeviceAction(String actionName, long delay) {
        deviceActionExecutor.schedule(() -> {
            try {
                DeviceAction deviceAction = getDeviceActionByName(actionName);
                deviceAction.doAction();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.SECONDS);
    }

    public abstract void onReceiveDeviceEvent(DeviceEvent deviceEvent);
}

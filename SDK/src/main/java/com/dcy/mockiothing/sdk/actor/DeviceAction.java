package com.dcy.mockiothing.sdk.actor;

public abstract class DeviceAction {
    private final DeviceActionMachine deviceActionMachine;

    protected DeviceAction(DeviceActionMachine deviceActionMachine) {
        this.deviceActionMachine = deviceActionMachine;
    }

    protected DeviceActionMachine getDeviceActionMachine() {
        return deviceActionMachine;
    }

    protected abstract void doAction() throws InterruptedException;

    protected void nextAction(String actionName, long delay) {
        deviceActionMachine.executeDeviceAction(actionName, delay);
    }
}

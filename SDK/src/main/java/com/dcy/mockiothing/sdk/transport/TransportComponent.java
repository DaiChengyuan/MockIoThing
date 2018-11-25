package com.dcy.mockiothing.sdk.transport;

import com.dcy.mockiothing.sdk.handler.DeviceMessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TransportComponent {
    private TransportAgent parent;
    private final Map<String, String> transComponentParams = new LinkedHashMap<>();

    private boolean activity = true;
    private final ReentrantLock lock = new ReentrantLock();

    protected TransportComponent(TransportAgent parent) {
        this.parent = parent;
        addTransComponentParams();
    }

    protected TransportAgent getParentTransportAgent() {
        return parent;
    }

    protected void setParentTransportAgent(TransportAgent parent) {
        this.parent = parent;
    }

    protected void setTransComponentParams(Map<String, String> transportDataPoints) {
        for (Map.Entry<String, String> entry : transportDataPoints.entrySet()) {
            transComponentParams.put(entry.getKey(), entry.getValue());
        }
    }

    protected Map<String, String> getTransComponentParams() {
        return transComponentParams;
    }

    protected String getTransComponentParam(String paramName) {
        return transComponentParams.get(transParamName(paramName));
    }

    protected void setTransComponentParam(String paramName, String paramValue) {
        transComponentParams.put(transParamName(paramName), paramValue);
    }

    protected String transParamName(String paramName) {
        return parent.getTransportAgentName() + paramName;
    }

    protected boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    protected ReentrantLock getLock() {
        return lock;
    }

    protected abstract void addTransComponentParams();

    protected abstract void init(Map<String, String> transportDataPoints, TransportAgent.CallBack callBack);

    protected abstract void open() throws InterruptedException;

    protected abstract void shut() throws InterruptedException;

    protected abstract void send(DeviceMessage msg) throws InterruptedException;

    protected abstract void recv() throws InterruptedException;

    @Override
    public String toString() {
        return transComponentParams.toString();
    }
}

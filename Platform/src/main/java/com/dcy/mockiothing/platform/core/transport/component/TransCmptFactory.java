package com.dcy.mockiothing.platform.core.transport.component;

import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.transport.TransportComponent;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.dcy.mockiothing.sdk.transport.TransportParams.Tcp;

public class TransCmptFactory {

    public static TransportComponent newTransportComponent(TransportAgent transportAgent,
                                                           Map<String, String> deviceDataPoints) {
        switch (transportAgent.getTransportAgentType()) {
        case TCP_CLIENT:
            return new TcpClientCmpt(transportAgent);
        case TCP_SERVER:
            return new TcpServerCmpt(transportAgent);
        case TCP_SERVER_SHARED:
            return getSharedTcpServerCmpt(transportAgent, deviceDataPoints);
        default:
            return null;
        }
    }

    private static Map<String, SharedTcpServerCmpt> stringSharedTcpServerCmptMap = new HashMap<>();

    private static SharedTcpServerCmpt getSharedTcpServerCmpt(TransportAgent transportAgent,
                                                              Map<String, String> deviceDataPoints) {
        SharedTcpServerCmpt sharedTcpServerCmpt;
        String sharedTcpServerPort = null;
        if (deviceDataPoints != null) {
            sharedTcpServerPort = deviceDataPoints.get(transportAgent.getTransportAgentName() + Tcp.LocalPort);
        }
        if (deviceDataPoints == null || StringUtils.isEmpty(sharedTcpServerPort)) {
            String transportAgentName = transportAgent.getTransportAgentName();
            sharedTcpServerCmpt = stringSharedTcpServerCmptMap.get(transportAgentName);
            if (sharedTcpServerCmpt == null) {
                sharedTcpServerCmpt = new SharedTcpServerCmpt(transportAgent);
                stringSharedTcpServerCmptMap.put(transportAgentName, sharedTcpServerCmpt);
            }
            sharedTcpServerCmpt.setActivity(false);
        } else {
            sharedTcpServerCmpt = stringSharedTcpServerCmptMap.get(sharedTcpServerPort);
            if (sharedTcpServerCmpt == null) {
                sharedTcpServerCmpt = new SharedTcpServerCmpt(transportAgent);
                stringSharedTcpServerCmptMap.put(sharedTcpServerPort, sharedTcpServerCmpt);
            }
            sharedTcpServerCmpt.setActivity(true);
        }
        return sharedTcpServerCmpt;
    }
}

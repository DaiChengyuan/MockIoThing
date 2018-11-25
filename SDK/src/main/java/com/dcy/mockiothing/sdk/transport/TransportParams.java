package com.dcy.mockiothing.sdk.transport;

public class TransportParams {
    public enum TYPE {
        TCP_CLIENT,
        TCP_SERVER,
        TCP_SERVER_SHARED
    }

    public final static TcpComponentParams Tcp = new TcpComponentParams();
    public final static class TcpComponentParams {
        public String LocalAddr = "_local_addr";
        public String LocalPort = "_local_port";
        public String RemoteAddr = "_remote_addr";
        public String RemotePort = "_remote_port";
        public String ConnStatus = "_status";
        public String CtrlSwitch = "_switch";
    }
}

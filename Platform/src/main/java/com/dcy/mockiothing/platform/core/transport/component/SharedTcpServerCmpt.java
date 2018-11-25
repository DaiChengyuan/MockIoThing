package com.dcy.mockiothing.platform.core.transport.component;

import com.dcy.mockiothing.platform.common.util.SpringUtil;
import com.dcy.mockiothing.platform.core.transport.nettychannel.NettyChannelAttribute;
import com.dcy.mockiothing.sdk.DeviceDataPoints;
import com.dcy.mockiothing.sdk.DeviceModel;
import com.dcy.mockiothing.sdk.handler.DeviceMessage;
import com.dcy.mockiothing.sdk.handler.DeviceMessageDecoder;
import com.dcy.mockiothing.sdk.handler.DeviceMessageEncoder;
import com.dcy.mockiothing.sdk.handler.DeviceMessageHandler;
import com.dcy.mockiothing.sdk.transport.TransportAgent;
import com.dcy.mockiothing.sdk.transport.TransportComponent;
import com.dcy.mockiothing.sdk.transport.TransportParams;
import com.dcy.mockiothing.sdk.utils.ByteQueue;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static com.dcy.mockiothing.sdk.transport.TransportParams.Tcp;
import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_STATUS;
import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_STATUS_OFF;
import static com.dcy.mockiothing.sdk.utils.Contants.DEVICE_UUID;

public class SharedTcpServerCmpt extends TransportComponent {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ServerBootstrap serverBootstrap = (ServerBootstrap) SpringUtil.getBean("serverBootstrap");
    private Channel serverChannel;

    private final Map<String, TransportAgent> transportAgentMap = new HashMap<>();

    private final SharedTcpServerAgent sharedTcpServerAgent;

    class SharedTcpServerAgent extends TransportAgent {
        private Map<String, TransportAgent> transportAgentMap;
        private DeviceMessageDecoder deviceMessageDecoder;
        private DeviceMessageEncoder deviceMessageEncoder;
        public SharedTcpServerAgent(Map<String, TransportAgent> transportAgentMap,
                                    DeviceMessageDecoder deviceMessageDecoder,
                                    DeviceMessageEncoder deviceMessageEncoder) {
            super("shared_transport_agent", TransportParams.TYPE.TCP_SERVER_SHARED, null);
            this.transportAgentMap = transportAgentMap;
            this.deviceMessageDecoder = deviceMessageDecoder;
            this.deviceMessageEncoder = deviceMessageEncoder;
        }
        @Override
        protected DeviceMessageDecoder addDeviceMessageDecoder() {
            return new DeviceMessageDecoder(this) {
                @Override
                public DeviceMessage decode(ByteQueue in) {
                    return deviceMessageDecoder.decode(in);
                }
            };
        }
        @Override
        protected DeviceMessageEncoder addDeviceMessageEncoder() {
            return new DeviceMessageEncoder(this) {
                @Override
                public ByteQueue encode(DeviceMessage msg) {
                    return deviceMessageEncoder.encode(msg);
                }
            };
        }
        @Override
        protected DeviceMessageHandler addDeviceMessageHandler() {
            return new DeviceMessageHandler(this) {
                @Override
                public DeviceMessage handleMsg(DeviceMessage deviceMessage) {
                    switch (deviceMessage.getDeviceMessageType()) {
                    case MSG_ON_OPEN:
                        return null;
                    case MSG_ON_READ:
                        TransportAgent transportAgent = getTransportAgent(deviceMessage.getDeviceIdentification());
                        if (transportAgent != null) {
                            return transportAgent.getDeviceMessageHandler().handleMsg(deviceMessage);
                        }
                    case MSG_ON_SHUT:
                    default:
                        return null;
                    }
                }
            };
        }
        private TransportAgent getTransportAgent(String deviceIdentification) {
            return transportAgentMap.get(deviceIdentification);
        }
    }

    protected SharedTcpServerCmpt(TransportAgent parent) {
        super(parent);
        DeviceMessageDecoder deviceMessageDecoder = super.getParentTransportAgent().getDeviceMessageDecoder();
        DeviceMessageEncoder deviceMessageEncoder = super.getParentTransportAgent().getDeviceMessageEncoder();
        sharedTcpServerAgent = new SharedTcpServerAgent(transportAgentMap, deviceMessageDecoder, deviceMessageEncoder);
    }

    @Override
    protected void addTransComponentParams() {
        super.setTransComponentParam(Tcp.LocalAddr, "127.0.0.1");
        super.setTransComponentParam(Tcp.LocalPort, "");
        super.setTransComponentParam(Tcp.CtrlSwitch, "1");
        super.setTransComponentParam(Tcp.ConnStatus, "0");
    }

    @Override
    protected void init(Map<String, String> transportDataPoints, TransportAgent.CallBack callBack) {
        if (transportDataPoints != null) {
            super.setTransComponentParams(transportDataPoints);
            if (!super.isActivity() && !StringUtils.isEmpty(super.getTransComponentParam(Tcp.LocalPort))) {
                DeviceModel deviceModel = super.getParentTransportAgent().getParentDeviceModel();
                DeviceDataPoints deviceDataPoints = deviceModel.getDeviceDataPoints();
                deviceDataPoints.updDeviceDataPoint(DEVICE_STATUS, DEVICE_STATUS_OFF);
                log.info("shared transport component to be changed, reset devicemock instance. } {}",
                        deviceDataPoints.getDeviceDataPoint(DEVICE_UUID));
            }
        }
        updConnStatus("init", super.getParentTransportAgent());
    }

    @Override
    protected void open() throws InterruptedException {
        TransportAgent transportAgent = super.getParentTransportAgent();
        DeviceDataPoints deviceDataPoints = transportAgent.getParentDeviceModel().getDeviceDataPoints();
        if (deviceDataPoints.getDeviceDataPoint(DEVICE_STATUS).equals(DEVICE_STATUS_OFF)) {
            return;
        }
        String deviceIdentification = genDeviceIdentification(deviceDataPoints.getDeviceIdentificationPoint());
        if (serverChannel != null) {
            if (transportAgentMap.get(deviceIdentification) == null) {
                transportAgentMap.put(deviceIdentification, transportAgent);
                updConnStatus("1", transportAgent);
            } else {
                log.warn("conflict: " + deviceIdentification);
                updConnStatus("conflict: " + deviceIdentification, transportAgent);
            }
            return;
        }
        log.debug("new SharedTcpServerCmpt...");
        if (transportAgentMap.isEmpty()) {
            InetSocketAddress localAddress = null;
            try {
                String localPort = super.getTransComponentParam(Tcp.LocalPort);
                if (StringUtils.isEmpty(localPort)) {
                    updConnStatus("empty port", transportAgent);
                    return;
                }
                localAddress = new InetSocketAddress(Integer.parseInt(localPort));
            } catch (Exception e) {
                e.printStackTrace();
            }
            doBind(localAddress, deviceIdentification);
        }
    }

    @Override
    protected void shut() throws InterruptedException {
        TransportAgent transportAgent = super.getParentTransportAgent();
        DeviceDataPoints deviceDataPoints = transportAgent.getParentDeviceModel().getDeviceDataPoints();
        String deviceIdentification = genDeviceIdentification(deviceDataPoints.getDeviceIdentificationPoint());
        transportAgentMap.remove(deviceIdentification);
        if (transportAgentMap.isEmpty()) {
            if (serverChannel != null) {
                serverChannel.close();
            }
        } else {
            updConnStatus("0", transportAgent);
        }
    }

    @Override
    protected void send(DeviceMessage msg) throws InterruptedException {

    }

    @Override
    protected void recv() throws InterruptedException {

    }

    private void doBind(InetSocketAddress localAddress, String deviceIdentification) throws InterruptedException {
        if (localAddress == null) {
            updConnStatus("invalid port", super.getParentTransportAgent());
            return;
        }
        if (!super.getTransComponentParam(Tcp.CtrlSwitch).equals("1")) {
            return;
        }
        transportAgentMap.put(deviceIdentification, super.getParentTransportAgent());
        serverBootstrap.bind(localAddress).addListener((ChannelFutureListener) future -> {
            TransportAgent transportAgent = transportAgentMap.get(deviceIdentification);
            Throwable cause = future.cause();
            if (cause != null) {
                log.error("connect err! cause: {}", cause.toString());
                updConnStatus(cause.getMessage(), transportAgent);
                setServerChannel(null);
                transportAgentMap.remove(deviceIdentification);
            } else {
                setServerChannel(future.channel());
                log.debug("connect begin! {}", serverChannel.toString());
                updConnStatus("1", transportAgent);
                setCloseFuture();
            }
        }).sync();
    }

    private void setCloseFuture() {
        serverChannel.closeFuture().addListener((ChannelFutureListener) future -> {
            log.debug("connect end! {}", serverChannel.toString());
            updConnStatus("0", super.getParentTransportAgent());
            setServerChannel(null);
        });
    }

    private void setServerChannel(Channel channel) {
        this.serverChannel = channel;
        if (channel != null) {
            setChannelAttr();
        }
    }

    private void setChannelAttr() {
        serverChannel.attr(NettyChannelAttribute.NETTY_CHANNEL_KEY).setIfAbsent(sharedTcpServerAgent);
    }

    private void updConnStatus(String status, TransportAgent transportAgent) {
        super.setTransComponentParam(Tcp.ConnStatus, status);
        DeviceDataPoints deviceDataPoints = transportAgent.getParentDeviceModel().getDeviceDataPoints();
        deviceDataPoints.updDeviceDataPoint(transportAgent.getTransportAgentName() + Tcp.ConnStatus, status);
    }

    private String genDeviceIdentification(String deviceIdentification) {
        return super.getParentTransportAgent().getTransportAgentName() + "@" + deviceIdentification;
    }
}

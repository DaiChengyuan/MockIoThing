package com.dcy.mockiothing.sdk.actor;

import java.util.EventObject;

public abstract class DeviceEvent extends EventObject {
    public DeviceEvent(Object source) {
        super(source);
    }

    public static class DeviceCreatedEvent extends DeviceEvent {
        public DeviceCreatedEvent(Object source) {
            super(source);
        }
    }

    public static class DeviceUpdatedEvent extends DeviceEvent {
        public DeviceUpdatedEvent(Object source) {
            super(source);
        }
    }

    public static class DeviceDeletedEvent extends DeviceEvent {
        public DeviceDeletedEvent(Object source) {
            super(source);
        }
    }

    public static class TransportCreatedEvent extends DeviceEvent {
        public TransportCreatedEvent(Object source) {
            super(source);
        }
    }

    public static class TransportUpdatedEvent extends DeviceEvent {
        public TransportUpdatedEvent(Object source) {
            super(source);
        }
    }

    public static class TransportDeletedEvent extends DeviceEvent {
        public TransportDeletedEvent(Object source) {
            super(source);
        }
    }
}

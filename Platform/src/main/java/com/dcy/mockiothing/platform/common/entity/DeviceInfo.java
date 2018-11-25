package com.dcy.mockiothing.platform.common.entity;

import lombok.Data;

@Data
public class DeviceInfo {
    private String model;
    private String name;
    private String type;
    private String desc;
    private String uuid;
    private String status;
}

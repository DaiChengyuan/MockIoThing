package com.dcy.mockiothing.platform.service;

import com.dcy.mockiothing.platform.common.entity.DeviceInfo;
import com.dcy.mockiothing.platform.dao.DeviceInfoMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@MapperScan(value = {"com.dcy.mockiothing.dao"})
public class DeviceInfoService {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    public List<DeviceInfo> getDeviceInfoList() {
        return deviceInfoMapper.getDeviceInfoList();
    }

    public List<DeviceInfo> getDeviceInfoListByModel(String model) {
        return deviceInfoMapper.getDeviceInfoListByModel(model);
    }

    public void addDeviceInfo(DeviceInfo deviceInfo) {
        deviceInfoMapper.addDeviceInfo(deviceInfo);
    }

    public void updDeviceInfo(DeviceInfo deviceInfo) {
        deviceInfoMapper.updDeviceInfo(deviceInfo);
    }

    public void delDeviceInfo(String uuid) {
        deviceInfoMapper.delDeviceInfo(uuid);
    }
}

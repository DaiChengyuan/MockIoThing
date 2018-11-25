package com.dcy.mockiothing.platform.dao;

import com.dcy.mockiothing.platform.common.entity.DeviceInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceInfoMapper {

    public List<DeviceInfo> getDeviceInfoList();

    public List<DeviceInfo> getDeviceInfoListByModel(String model);

    public void addDeviceInfo(DeviceInfo deviceInfo);

    public void updDeviceInfo(DeviceInfo deviceInfo);

    public void delDeviceInfo(String uuid);

}

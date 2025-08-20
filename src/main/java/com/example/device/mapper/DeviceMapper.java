package com.example.device.mapper;

import com.example.device.dto.Device;
import com.example.device.entity.DeviceEntity;

import java.util.List;

public class DeviceMapper {

    public static Device toDto(DeviceEntity deviceEntity) {

        return Device.builder()
                .id(deviceEntity.getId())
                .name(deviceEntity.getName())
                .state(deviceEntity.getState())
                .brand(deviceEntity.getBrand())
                .createdAt(deviceEntity.getCreatedAt())
                .build();
    }

    public static DeviceEntity toEntity(Device device) {
        return DeviceEntity.builder()
                .name(device.getName())
                .state(device.getState())
                .brand(device.getBrand())
                .build();
    }

    public static List<Device> toDtoList(List<DeviceEntity> deviceEntities) {
        return deviceEntities.stream().map(DeviceMapper::toDto).toList();
    }
}

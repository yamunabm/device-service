package com.example.device.service;

import com.example.device.dto.Device;
import com.example.device.entity.DeviceEntity;
import com.example.device.entity.DeviceState;
import com.example.device.exception.DeviceInUseException;
import com.example.device.exception.DeviceNotFoundException;
import com.example.device.mapper.DeviceMapper;
import com.example.device.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;


    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device saveDevice(Device device) {
        DeviceEntity savedDevice = this.deviceRepository.save(DeviceMapper.toEntity(device));
        return DeviceMapper.toDto(savedDevice);
    }

    public Device getDeviceById(UUID id) {
        var deviceEntity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));

        return DeviceMapper.toDto(deviceEntity);
    }

    public List<Device> findAll() {
        List<DeviceEntity> deviceEntities = this.deviceRepository.findAll();

        return DeviceMapper.toDtoList(deviceEntities);
    }

    public List<Device> findAllDevicesByBrand(String brand) {
        List<DeviceEntity> deviceEntities = this.deviceRepository.findByBrand(brand);
        return DeviceMapper.toDtoList(deviceEntities);
    }

    public List<Device> findAllDevicesByState(DeviceState state) {
        List<DeviceEntity> deviceEntities = this.deviceRepository.findByState(state.name());
        return DeviceMapper.toDtoList(deviceEntities);
    }

    public void deleteDeviceById(UUID id) {
        var deviceEntity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));

        if (deviceEntity.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseException("Device in use cannot be deleted");
        }

        this.deviceRepository.deleteById(id);
    }

    public Device updateDevice(Device deviceToUpdate) {

        UUID id = deviceToUpdate.getId();
        var deviceEntity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));

        if (deviceEntity.getState() == DeviceState.IN_USE) {
            if (!deviceEntity.getName().equals(deviceToUpdate.getName()) || !deviceEntity.getBrand().equals(deviceToUpdate.getBrand())) {
                throw new DeviceInUseException("Cannot update name or brand when device is in use.");
            }
        }

        Optional.ofNullable(deviceToUpdate.getName())
                .ifPresent(deviceEntity::setName);

        Optional.ofNullable(deviceToUpdate.getBrand())
                .ifPresent(deviceEntity::setBrand);

        Optional.ofNullable(deviceToUpdate.getState())
                .ifPresent(deviceEntity::setState);

        return DeviceMapper.toDto(deviceRepository.save(deviceEntity));
    }
}



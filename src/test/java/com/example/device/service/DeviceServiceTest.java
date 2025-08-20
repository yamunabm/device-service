package com.example.device.service;

import com.example.device.dto.Device;
import com.example.device.entity.DeviceEntity;
import com.example.device.entity.DeviceState;
import com.example.device.exception.DeviceInUseException;
import com.example.device.exception.DeviceNotFoundException;
import com.example.device.mapper.DeviceMapper;
import com.example.device.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private UUID deviceId;
    private DeviceEntity deviceEntity;

    @BeforeEach
    void setUp() {
        deviceId = UUID.randomUUID();
        deviceEntity = new DeviceEntity();
        deviceEntity.setId(deviceId);
        deviceEntity.setName("Test Device");
        deviceEntity.setBrand("Test Brand");
        deviceEntity.setState(DeviceState.AVAILABLE);
    }

    @Test
    void testCreateDevice_success() {
        DeviceEntity entity = new DeviceEntity(deviceId, "Router", "Cisco", DeviceState.AVAILABLE, null);
        when(deviceRepository.save(any(DeviceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device toSave = DeviceMapper.toDto(entity);
        Device result = deviceService.saveDevice(toSave);

        assertEquals("Cisco", result.getBrand());
        verify(deviceRepository, times(1)).save(any(DeviceEntity.class));
    }

    @Test
    void testGetDeviceById_found() {
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(deviceEntity));

        var result = deviceService.getDeviceById(deviceId);

        assertEquals("Test Device", result.getName());
        verify(deviceRepository, times(1)).findById(deviceId);
    }

    @Test
    void testGetDeviceById_notFound() {
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> deviceService.getDeviceById(deviceId));

        verify(deviceRepository, times(1)).findById(deviceId);
    }

    @Test
    void testGetAllDevices_found() {
        when(deviceRepository.findAll()).thenReturn(Collections.singletonList(deviceEntity));

        var result = deviceService.findAll();

        assertFalse(result.isEmpty());
        verify(deviceRepository, times(1)).findAll();
    }

    @Test
    void testGetAllDevices_NotFound() {
        when(deviceRepository.findAll()).thenReturn(Collections.emptyList());
        var result = deviceService.findAll();
        assertTrue(result.isEmpty());

        verify(deviceRepository, times(1)).findAll();
    }

    @Test
    void testGetDevicesByBrand_found() {
        when(deviceRepository.findByBrand("Test Brand")).thenReturn(Collections.singletonList(deviceEntity));

        var result = deviceService.findAllDevicesByBrand("Test Brand");

        assertFalse(result.isEmpty());
        verify(deviceRepository, times(1)).findByBrand("Test Brand");
    }

    @Test
    void testGetDevicesByBrand_NotFound() {
        when(deviceRepository.findByBrand("Test Brand")).thenReturn(Collections.emptyList());

        var result = deviceService.findAllDevicesByBrand("Test Brand");

        assertTrue(result.isEmpty());
        verify(deviceRepository, times(1)).findByBrand("Test Brand");
    }


    @Test
    void testGetDevicesByState_Found() {
        when(deviceRepository.findByState(DeviceState.AVAILABLE.name())).thenReturn(Collections.singletonList(deviceEntity));
        var result = deviceService.findAllDevicesByState(DeviceState.AVAILABLE);
        assertFalse(result.isEmpty());

        verify(deviceRepository, times(1)).findByState(DeviceState.AVAILABLE.name());
    }

    @Test
    void testGetDevicesByState_NotFound() {
        when(deviceRepository.findByState(DeviceState.AVAILABLE.name())).thenReturn(Collections.emptyList());
        var result = deviceService.findAllDevicesByState(DeviceState.AVAILABLE);
        assertTrue(result.isEmpty());

        verify(deviceRepository, times(1)).findByState(DeviceState.AVAILABLE.name());
    }

    @Test
    void testDeleteDeviceById_inUseThrowsException() {
        UUID id = UUID.randomUUID();
        DeviceEntity entity = new DeviceEntity(id, "Laptop", "Dell", DeviceState.IN_USE, null);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(DeviceInUseException.class, () -> deviceService.deleteDeviceById(id));
        verify(deviceRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteDeviceById_NotFound() {
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.deleteDeviceById(deviceId));
        verify(deviceRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteDeviceById_success() {
        UUID id = UUID.randomUUID();
        DeviceEntity entity = new DeviceEntity(id, "Tablet", "Apple", DeviceState.AVAILABLE, null);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));

        deviceService.deleteDeviceById(id);

        verify(deviceRepository, times(1)).deleteById(id);
    }

    @Test
    void testUpdateDevice_inUseChangeStatus_success() {
        UUID id = UUID.randomUUID();
        DeviceEntity entity = new DeviceEntity(id, "Watch", "Garmin", DeviceState.IN_USE, null);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(deviceRepository.save(any(DeviceEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Device updated = DeviceMapper.toDto(entity);
        updated.setState(DeviceState.INACTIVE);

        Device result = deviceService.updateDevice(updated);

        assertEquals(DeviceState.INACTIVE, result.getState());
        verify(deviceRepository, times(1)).save(any(DeviceEntity.class));
    }

    @Test
    void testUpdateDevice_inUseNameChangeThrowsException() {
        UUID id = UUID.randomUUID();
        DeviceEntity entity = new DeviceEntity(id, "Watch", "Garmin", DeviceState.IN_USE, null);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));

        Device updated = DeviceMapper.toDto(entity);
        updated.setName("NewWatch");

        assertThrows(DeviceInUseException.class, () -> deviceService.updateDevice(updated));
    }

    @Test
    void testUpdateDevice_partialUpdate_success() {
        UUID id = UUID.randomUUID();
        DeviceEntity entity = new DeviceEntity(id, "Router", "Cisco", DeviceState.AVAILABLE, null);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(deviceRepository.save(any(DeviceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device updated = DeviceMapper.toDto(entity);
        updated.setBrand("Juniper");

        Device result = deviceService.updateDevice(updated);

        assertEquals("Juniper", result.getBrand());
        verify(deviceRepository, times(1)).save(any(DeviceEntity.class));
    }

    @Test
    void testUpdateDevice_fullUpdate_success() {
        UUID id = UUID.randomUUID();
        DeviceEntity entity = new DeviceEntity(id, "Router", "Cisco", DeviceState.AVAILABLE, null);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(deviceRepository.save(any(DeviceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device updated = DeviceMapper.toDto(entity);
        updated.setBrand("Juniper");
        updated.setName("Juniper Inc");
        updated.setState(DeviceState.IN_USE);

        Device result = deviceService.updateDevice(updated);

        assertEquals("Juniper", result.getBrand());
        verify(deviceRepository, times(1)).save(any(DeviceEntity.class));
    }
}

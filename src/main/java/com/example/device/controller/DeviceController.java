package com.example.device.controller;

import com.example.device.dto.Device;
import com.example.device.entity.DeviceState;
import com.example.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/device/v1")
@RestController
@Tag(name = "Device API", description = "Endpoints for managing devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Operation(summary = "Create a new device", description = "Saves a new device in the database")
    @PostMapping
    public ResponseEntity<Device> saveDevice(@RequestBody Device device) {
        Device savedDevice = this.deviceService.saveDevice(device);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
    }

    @Operation(summary = "Find device by id", description = "Fetches a new device from the database")
    @GetMapping("/id/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable UUID id) {
        Device deviceById = this.deviceService.getDeviceById(id);
        return ResponseEntity.status(HttpStatus.OK).body(deviceById);
    }

    @Operation(summary = "Find all devices", description = "Fetches all devices from the database")
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> allDevices = this.deviceService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(allDevices);
    }

    @Operation(summary = "Find all devices by brand", description = "Fetches all devices from the database matching the provided brand")
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Device>> getAllDevicesByBrand(@PathVariable String brand) {
        List<Device> allDevices = this.deviceService.findAllDevicesByBrand(brand);
        return ResponseEntity.status(HttpStatus.OK).body(allDevices);
    }

    @Operation(summary = "Find all devices by device state", description = "Fetches all devices from the database matching the provided device state")
    @GetMapping("/state/{state}")
    public ResponseEntity<List<Device>> getAllDevicesByState(@PathVariable String state) {
        List<Device> allDevices = this.deviceService.findAllDevicesByState(DeviceState.valueOf(state));
        return ResponseEntity.status(HttpStatus.OK).body(allDevices);
    }

    @Operation(summary = "Update device by id", description = "Update device by id in the database")
    @PatchMapping
    public ResponseEntity<Device> updateDevice(@RequestBody Device device) {
        Device updatedDevice = this.deviceService.updateDevice(device);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDevice);
    }

    @Operation(summary = "Delete device by id", description = "Delete device by id from the database")
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Device> deleteDevice(@PathVariable UUID id) {
        this.deviceService.deleteDeviceById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

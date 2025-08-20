package com.example.device.controller;

import com.example.device.dto.Device;
import com.example.device.entity.DeviceState;
import com.example.device.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceService deviceService;

    private Device device;
    private UUID deviceId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        deviceId = UUID.randomUUID();
        device = Device.builder()
                .id(deviceId)
                .name("Watch")
                .brand("Garmin")
                .state(DeviceState.AVAILABLE)
                .build();
    }

    @Test
    void testCreateDevice() throws Exception {
        when(deviceService.saveDevice(any(Device.class))).thenReturn(device);

        mockMvc.perform(post("/device/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Watch"))
                .andExpect(jsonPath("$.brand").value("Garmin"));
    }

    @Test
    void testGetDeviceById() throws Exception {
        when(deviceService.getDeviceById(deviceId)).thenReturn(device);

        mockMvc.perform(get("/device/v1/id/" + deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Watch"))
                .andExpect(jsonPath("$.brand").value("Garmin"));
    }

    @Test
    void testGetAllDevices() throws Exception {
        List<Device> devices = Collections.singletonList(device);
        when(deviceService.findAll()).thenReturn(devices);

        mockMvc.perform(get("/device/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Watch"));
    }

    @Test
    void testGetDevicesByBrand() throws Exception {
        List<Device> devices = Collections.singletonList(device);
        when(deviceService.findAllDevicesByBrand("Garmin")).thenReturn(devices);

        mockMvc.perform(get("/device/v1/brand/Garmin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Garmin"));
    }

    @Test
    void testGetDevicesByState() throws Exception {
        List<Device> devices = Collections.singletonList(device);
        when(deviceService.findAllDevicesByState(DeviceState.AVAILABLE)).thenReturn(devices);

        mockMvc.perform(get("/device/v1/state/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("AVAILABLE"));
    }

    @Test
    void testUpdateDevice() throws Exception {
        Device updatedDevice = Device.builder()
                .id(deviceId)
                .name("Watch")
                .brand("Garmin")
                .state(DeviceState.IN_USE)
                .build();

        when(deviceService.updateDevice(any(Device.class))).thenReturn(updatedDevice);

        mockMvc.perform(patch("/device/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDevice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("IN_USE"));
    }

    @Test
    void testDeleteDevice() throws Exception {
        doNothing().when(deviceService).deleteDeviceById(deviceId);

        mockMvc.perform(delete("/device/v1/id/" + deviceId))
                .andExpect(status().isOk());
    }
}

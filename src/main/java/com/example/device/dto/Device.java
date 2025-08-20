package com.example.device.dto;

import com.example.device.entity.DeviceState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Device {

    private UUID id;
    private String name;
    private String brand;
    private DeviceState state;
    private LocalDateTime createdAt;
}

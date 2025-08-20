package com.example.device.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "device")
public class DeviceEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20)
    private DeviceState state;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

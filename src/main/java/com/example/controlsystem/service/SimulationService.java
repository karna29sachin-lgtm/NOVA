package com.example.controlsystem.service;

import com.example.controlsystem.model.Vehicle;
import com.example.controlsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationService {

    private final VehicleRepository vehicleRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Run this method every 2 seconds
    @Scheduled(fixedRate = 2000)
    public void simulateVehicleMovement() {
        // Fetch all vehicles
        List<Vehicle> vehicles = vehicleRepository.findAll();

        boolean anyMoved = false;

        for (Vehicle vehicle : vehicles) {
            // Only move vehicles that are in transit
            if ("IN_TRANSIT".equalsIgnoreCase(vehicle.getStatus())) {
                // Simulate movement by incrementing coordinates slightly
                double latOffset = (Math.random() - 0.5) * 0.001; // Tiny random movement
                double lonOffset = (Math.random() - 0.5) * 0.001;

                vehicle.setLatitude(vehicle.getLatitude() + latOffset);
                vehicle.setLongitude(vehicle.getLongitude() + lonOffset);

                vehicleRepository.save(vehicle);
                anyMoved = true;
                log.debug("Moved vehicle {}: new coords {}, {}", vehicle.getVehicleId(), vehicle.getLatitude(), vehicle.getLongitude());
            }
        }

        // Broadcast telemetry to connected WebSocket clients
        if (anyMoved || !vehicles.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/telemetry", vehicles);
        }
    }
}

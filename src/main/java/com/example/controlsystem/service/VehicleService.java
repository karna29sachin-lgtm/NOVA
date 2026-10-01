package com.example.controlsystem.service;

import com.example.controlsystem.model.Vehicle;
import com.example.controlsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle registerVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle updateVehicleStatus(Long id, String status) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setStatus(status);
        return vehicleRepository.save(vehicle);
    }
    
    public Vehicle updateVehicleLocation(Long id, double lat, double lon) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setLatitude(lat);
        vehicle.setLongitude(lon);
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}

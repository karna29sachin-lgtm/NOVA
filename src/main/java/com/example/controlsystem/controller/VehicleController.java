package com.example.controlsystem.controller;

import com.example.controlsystem.model.Vehicle;
import com.example.controlsystem.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle Management API", description = "Endpoints for managing vehicles (secured with JWT)")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new vehicle", description = "Admin only. Add a new vehicle to the system.")
    public ResponseEntity<Vehicle> registerVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.registerVehicle(vehicle));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'MAINTENANCE_TECHNICIAN')")
    @Operation(summary = "Get all vehicles", description = "Returns a list of all vehicles.")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'MAINTENANCE_TECHNICIAN')")
    @Operation(summary = "Get a vehicle by ID", description = "Fetch details of a single vehicle.")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTENANCE_TECHNICIAN')")
    public ResponseEntity<Vehicle> updateVehicleStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }
    
    @PatchMapping("/{id}/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<Vehicle> updateVehicleLocation(@PathVariable Long id, @RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(vehicleService.updateVehicleLocation(id, lat, lon));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a vehicle", description = "Admin only. Remove a vehicle from the system.")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}

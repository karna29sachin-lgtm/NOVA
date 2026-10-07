package com.example.controlsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.*;

@RestController
@RequestMapping("/api/mock")
@CrossOrigin(origins = "*")
public class MockDataController {

    @GetMapping("/users")
    public List<Map<String, Object>> getUsers() {
        return Arrays.asList(
            Map.of("id", "USR-001", "name", "Sarah Jenkins", "role", "Fleet Admin", "status", "ONLINE", "lastActive", "Just now", "department", "Operations", "avatar", "https://i.pravatar.cc/150?u=1"),
            Map.of("id", "USR-002", "name", "Marcus Chen", "role", "Lead Technician", "status", "ONLINE", "lastActive", "5m ago", "department", "Maintenance", "avatar", "https://i.pravatar.cc/150?u=2"),
            Map.of("id", "USR-003", "name", "Elena Rodriguez", "role", "Safety Officer", "status", "OFFLINE", "lastActive", "2h ago", "department", "Safety & Compliance", "avatar", "https://i.pravatar.cc/150?u=3"),
            Map.of("id", "USR-004", "name", "David Kim", "role", "Software Engineer", "status", "ONLINE", "lastActive", "1m ago", "department", "Engineering", "avatar", "https://i.pravatar.cc/150?u=4")
        );
    }

    @GetMapping("/alerts")
    public List<Map<String, Object>> getAlerts() {
        return Arrays.asList(
            Map.of("id", "ALT-892", "type", "CRITICAL", "title", "LiDAR Sensor Degradation", "vehicle", "Unit 042", "time", "2 mins ago", "description", "Forward-facing LiDAR array reporting 30% packet loss. Vision fallback engaged."),
            Map.of("id", "ALT-891", "type", "WARNING", "title", "Battery Temperature", "vehicle", "Unit 018", "time", "15 mins ago", "description", "Cell pack 4 operating at 42°C, approaching thermal threshold."),
            Map.of("id", "ALT-890", "type", "WARNING", "title", "Unexpected Route Deviation", "vehicle", "Unit 055", "time", "42 mins ago", "description", "Vehicle altered route due to localized flooding detected by swarm network."),
            Map.of("id", "ALT-889", "type", "INFO", "title", "Firmware Update Complete", "vehicle", "Fleet-wide", "time", "2 hours ago", "description", "NOVA OS v4.2.1 successfully deployed to 142 vehicles.")
        );
    }

    @GetMapping("/issues")
    public List<Map<String, Object>> getIssues() {
        return Arrays.asList(
            Map.of("id", "TKT-4921", "title", "LiDAR Calibration Drift", "vehicle", "Unit 042", "priority", "HIGH", "status", "OPEN", "assignedTo", "Marcus Chen", "reported", "2 hours ago"),
            Map.of("id", "TKT-4920", "title", "Inverter Coolant Low", "vehicle", "Unit 018", "priority", "MEDIUM", "status", "IN PROGRESS", "assignedTo", "Sarah Jenkins", "reported", "5 hours ago"),
            Map.of("id", "TKT-4919", "title", "Cabin Camera Obscured", "vehicle", "Unit 104", "priority", "LOW", "status", "OPEN", "assignedTo", "Unassigned", "reported", "1 day ago"),
            Map.of("id", "TKT-4918", "title", "Steering Actuator Fault", "vehicle", "Unit 007", "priority", "CRITICAL", "status", "RESOLVED", "assignedTo", "Alex Wong", "reported", "2 days ago")
        );
    }

    @GetMapping("/trips")
    public List<Map<String, Object>> getTrips() {
        return Arrays.asList(
            Map.ofEntries(
                Map.entry("id", "TRP-1042"), Map.entry("origin", "Delhi"), Map.entry("destination", "Greater Noida"), 
                Map.entry("distance", "42.6 km"), Map.entry("duration", "38 min"), Map.entry("status", "AUTONOMOUS"), 
                Map.entry("date", "02 OCT 2026"), Map.entry("avgSpeed", "67.2 km/h"), Map.entry("energyConsumed", "6.1 kWh"), 
                Map.entry("autonomousPercent", 100), Map.entry("interventions", 0), Map.entry("sensorEvents", 2), Map.entry("alerts", 0)
            ),
            Map.ofEntries(
                Map.entry("id", "TRP-1041"), Map.entry("origin", "Greater Noida"), Map.entry("destination", "Noida"), 
                Map.entry("distance", "31.2 km"), Map.entry("duration", "29 min"), Map.entry("status", "AUTONOMOUS"), 
                Map.entry("date", "01 OCT 2026"), Map.entry("avgSpeed", "64.5 km/h"), Map.entry("energyConsumed", "4.4 kWh"), 
                Map.entry("autonomousPercent", 100), Map.entry("interventions", 0), Map.entry("sensorEvents", 0), Map.entry("alerts", 0)
            ),
            Map.ofEntries(
                Map.entry("id", "TRP-1040"), Map.entry("origin", "Noida"), Map.entry("destination", "Delhi"), 
                Map.entry("distance", "28.4 km"), Map.entry("duration", "34 min"), Map.entry("status", "MANUAL INTERVENTION"), 
                Map.entry("date", "30 SEP 2026"), Map.entry("avgSpeed", "50.1 km/h"), Map.entry("energyConsumed", "4.8 kWh"), 
                Map.entry("autonomousPercent", 88), Map.entry("interventions", 1), Map.entry("sensorEvents", 12), Map.entry("alerts", 1)
            )
        );
    }
}

package com.example.controlsystem.service;

import com.example.controlsystem.model.Route;
import com.example.controlsystem.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final PathfindingService pathfindingService;

    public Route createRoute(Route route) {
        route.setStatus("PENDING");
        
        // Use JGraphT to calculate optimal path and distance
        if (route.getStartLocation() != null && route.getEndLocation() != null) {
            PathfindingService.OptimalRouteResult result = 
                pathfindingService.findOptimalRoute(route.getStartLocation(), route.getEndLocation());
            
            route.setOptimalPath(result.pathDescription());
            route.setDistance(result.totalDistance());
        }
        
        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
    }

    public List<Route> getRoutesByVehicleId(Long vehicleId) {
        return routeRepository.findByVehicleId(vehicleId);
    }

    public Route updateRouteStatus(Long id, String status) {
        Route route = getRouteById(id);
        route.setStatus(status);
        return routeRepository.save(route);
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
}

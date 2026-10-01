package com.example.controlsystem.service;

import jakarta.annotation.PostConstruct;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathfindingService {

    private Graph<String, DefaultWeightedEdge> mapGraph;
    private DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraAlg;

    @PostConstruct
    public void init() {
        // Initialize a simple weighted graph representing our routing map
        mapGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Add typical control system nodes (e.g., warehouse zones, checkpoints)
        String[] nodes = {"ZoneA", "ZoneB", "ZoneC", "ZoneD", "ZoneE", "Gate1", "Gate2"};
        for (String node : nodes) {
            mapGraph.addVertex(node);
        }

        // Add weighted edges (representing distance or travel time)
        addEdge("ZoneA", "ZoneB", 5.0);
        addEdge("ZoneA", "ZoneC", 8.0);
        addEdge("ZoneB", "ZoneD", 3.0);
        addEdge("ZoneC", "ZoneD", 4.0);
        addEdge("ZoneD", "ZoneE", 2.0);
        addEdge("ZoneE", "Gate1", 6.0);
        addEdge("ZoneC", "Gate2", 7.0);
        addEdge("ZoneB", "Gate1", 10.0);

        // Initialize Dijkstra's algorithm instance
        dijkstraAlg = new DijkstraShortestPath<>(mapGraph);
    }

    private void addEdge(String source, String target, double weight) {
        DefaultWeightedEdge edge = mapGraph.addEdge(source, target);
        if (edge != null) {
            mapGraph.setEdgeWeight(edge, weight);
        }
    }

    public OptimalRouteResult findOptimalRoute(String startLocation, String endLocation) {
        if (!mapGraph.containsVertex(startLocation) || !mapGraph.containsVertex(endLocation)) {
            throw new IllegalArgumentException("Start or end location does not exist in the routing map.");
        }

        GraphPath<String, DefaultWeightedEdge> path = dijkstraAlg.getPath(startLocation, endLocation);

        if (path == null) {
            throw new RuntimeException("No path exists between " + startLocation + " and " + endLocation);
        }

        List<String> vertexList = path.getVertexList();
        double distance = path.getWeight();

        return new OptimalRouteResult(String.join(" -> ", vertexList), distance);
    }

    // Helper record for the result
    public record OptimalRouteResult(String pathDescription, double totalDistance) {}
}

package com.example.controlsystem.controller;

import com.example.controlsystem.service.ObstacleDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/vision")
@SuppressWarnings("null")
@RequiredArgsConstructor
@Tag(name = "Vision API", description = "Endpoints for camera and vision integration")
public class VisionController {

    private final ObstacleDetectionService obstacleDetectionService;

    @PostMapping(value = "/detect-obstacles", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    @Operation(summary = "Detect obstacles in image", description = "Upload a camera frame to detect objects using OpenCV")
    public ResponseEntity<ObstacleDetectionService.DetectionResult> detectObstacles(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ObstacleDetectionService.DetectionResult(false, 0, "Empty file provided"));
        }

        try {
            // Save the uploaded file temporarily to process it with OpenCV
            File tempFile = File.createTempFile("vision-upload-", ".jpg");
            file.transferTo(tempFile);

            // Run detection
            ObstacleDetectionService.DetectionResult result = obstacleDetectionService.detectObstacles(tempFile.getAbsolutePath());
            
            // Clean up
            tempFile.delete();

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new ObstacleDetectionService.DetectionResult(false, 0, "Failed to process image: " + e.getMessage()));
        }
    }
}

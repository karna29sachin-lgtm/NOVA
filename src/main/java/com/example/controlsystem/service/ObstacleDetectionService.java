package com.example.controlsystem.service;

import lombok.extern.slf4j.Slf4j;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.springframework.stereotype.Service;


import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
@Slf4j
public class ObstacleDetectionService {

    /**
     * Simulates vision-based obstacle detection using an input image path.
     * In a real scenario, this would be a frame from a vehicle camera (WebSocket or MJPEG stream).
     */
    public DetectionResult detectObstacles(String imagePath) {
        log.info("Starting obstacle detection on image: {}", imagePath);
        
        // Read the image using OpenCV
        Mat src = imread(imagePath);
        if (src == null || src.empty()) {
            log.error("Could not load image at path: {}", imagePath);
            return new DetectionResult(false, 0, "Image not found or invalid format");
        }

        // Convert to grayscale
        Mat gray = new Mat();
        cvtColor(src, gray, COLOR_BGR2GRAY);

        // Apply a Gaussian blur to reduce noise
        Mat blurred = new Mat();
        GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // Apply binary threshold (this simulates looking for dark obstacles on a light road, or vice versa)
        Mat thresh = new Mat();
        threshold(blurred, thresh, 100, 255, THRESH_BINARY_INV);

        // Find contours (potential obstacles)
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        findContours(thresh, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        int obstacleCount = 0;
        
        // Filter contours by area to ignore tiny noise
        for (long i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            double area = contourArea(contour);
            if (area > 500.0) { // arbitrary threshold for minimum obstacle size
                obstacleCount++;
                Rect boundingRect = boundingRect(contour);
                // In a real app, we might draw bounding boxes here
                rectangle(src, new Point(boundingRect.x(), boundingRect.y()), 
                          new Point(boundingRect.x() + boundingRect.width(), boundingRect.y() + boundingRect.height()), 
                          new Scalar(0, 0, 255, 0), 2, LINE_8, 0);
            }
        }
        
        // Optionally save the output image to verify
        // imwrite("output_detection.jpg", src);

        boolean obstacleDetected = obstacleCount > 0;
        String message = obstacleDetected ? 
                "Detected " + obstacleCount + " obstacle(s) in path." : 
                "Path is clear.";
                
        log.info("Detection finished: {}", message);
        return new DetectionResult(obstacleDetected, obstacleCount, message);
    }

    public record DetectionResult(boolean obstacleDetected, int obstacleCount, String message) {}
}

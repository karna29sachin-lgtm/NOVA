package com.example.controlsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ControlSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControlSystemApplication.class, args);
    }
}

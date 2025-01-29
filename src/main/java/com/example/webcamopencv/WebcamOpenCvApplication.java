package com.example.webcamopencv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebcamOpenCvApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebcamOpenCvApplication.class, args);
    }

}

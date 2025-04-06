package com.example.webcamopencv.Configuration;

import org.springframework.context.annotation.Configuration;
 
import jakarta.annotation.PostConstruct;

@Configuration
public class OpenCVConfig { 

    @PostConstruct
    public void init() { 
        System.setProperty("java.library.path", "D:\\OpenCv\\opencv\\build\\java\\x64");
        System.load("D:\\OpenCv\\opencv\\build\\java\\x64\\opencv_java4100.dll"); 

    }

}

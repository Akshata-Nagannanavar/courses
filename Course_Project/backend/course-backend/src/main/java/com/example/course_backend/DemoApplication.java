
package com.example.course_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Simple GET endpoint
    @GetMapping("/hello")
    public String hello() {
        return "Spring Boot is running alright!";


    }
}



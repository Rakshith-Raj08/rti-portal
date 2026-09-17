package com.example.rti_portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RtiPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtiPortalApplication.class, args);
	}

}

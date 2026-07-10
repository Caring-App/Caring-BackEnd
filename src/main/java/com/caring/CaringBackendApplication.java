package com.caring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CaringBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaringBackendApplication.class, args);
	}

}

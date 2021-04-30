package com.example.microservices.eproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(EProductApplication.class, args);
	}

}

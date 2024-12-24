package com.Maanas.Temp_OrderService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TempOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TempOrderServiceApplication.class, args);
	}

}

package com.example.morobooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MorobooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(MorobooksApplication.class, args);
	}

}

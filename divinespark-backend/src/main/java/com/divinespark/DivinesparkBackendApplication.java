package com.divinespark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DivinesparkBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DivinesparkBackendApplication.class, args);
	}

}

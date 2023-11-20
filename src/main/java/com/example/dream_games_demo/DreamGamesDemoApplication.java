package com.example.dream_games_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.dream_games_demo.service"})
public class DreamGamesDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamGamesDemoApplication.class, args);
	}
}


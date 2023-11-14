package com.example.dream_games_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication
public class DreamGamesDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamGamesDemoApplication.class, args);
	}
}


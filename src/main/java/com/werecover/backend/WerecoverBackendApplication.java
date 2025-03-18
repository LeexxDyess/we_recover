package com.werecover.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // ✅ Import this

@SpringBootApplication
@EnableScheduling // ✅ Enable scheduling for cron jobs
public class WerecoverBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(WerecoverBackendApplication.class, args);
	}
}

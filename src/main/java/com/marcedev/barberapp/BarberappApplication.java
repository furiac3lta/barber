package com.marcedev.barberapp;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BarberappApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarberappApplication.class, args);

	}

	@Bean
	CommandLineRunner printPasswordHash() {
		return args -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			System.out.println("BCrypt mCol.123 => " + encoder.encode("mCol.123"));
		};
	}

	@PostConstruct
	public void testDriver() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("✅ DRIVER CARGADO");
	}

}

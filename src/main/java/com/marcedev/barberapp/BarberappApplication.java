package com.marcedev.barberapp;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BarberappApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarberappApplication.class, args);

	}
	@PostConstruct
	public void testDriver() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("✅ DRIVER CARGADO");
	}

}

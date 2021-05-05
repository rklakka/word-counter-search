package com.test.coding.optus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OptusApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptusApplication.class, args);
	}

}

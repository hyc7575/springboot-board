package com.springbootboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringbootBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBoardApplication.class, args);
	}

}

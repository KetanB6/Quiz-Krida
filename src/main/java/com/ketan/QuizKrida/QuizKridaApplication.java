package com.ketan.QuizKrida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class QuizKridaApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizKridaApplication.class, args);
	}

}

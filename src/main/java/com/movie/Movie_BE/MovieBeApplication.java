package com.movie.Movie_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MovieBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieBeApplication.class, args);
	}

}

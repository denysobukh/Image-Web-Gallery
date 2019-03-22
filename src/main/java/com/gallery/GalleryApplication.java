package com.gallery;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;


@SpringBootApplication
public class Gallery {
	public static void main(String[] args) {
		SpringApplication.run(Gallery.class, args);
	}
}

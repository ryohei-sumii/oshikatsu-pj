package com.oshikatsu_pj.oshikatsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class OshikatsuApplication {

	public static void main(String[] args) {
		SpringApplication.run(OshikatsuApplication.class, args);
	}

}

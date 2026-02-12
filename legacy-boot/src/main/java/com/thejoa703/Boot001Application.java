package com.thejoa703; 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class Boot001Application { 
	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
		//Dotenv dotenv = Dotenv.configure() .directory("/home/ubuntu/legacy-boot").load();
		dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
		
		System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
		SpringApplication.run(Boot001Application.class, args);
	}

}
 

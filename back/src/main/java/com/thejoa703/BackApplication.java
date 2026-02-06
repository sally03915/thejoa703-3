package com.thejoa703;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}

}
/*
1. REDIS 키고
docker exec  -it  my-redis  redis-cli
docker exec  -it  my-redis  redis-cli  FLUSHALL

keys  *

2.  
http://localhost:8484/swagger-ui/index.html
 
*/
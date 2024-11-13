package com.work.here;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Auditing 기능 활성화
public class HereApplication {

	public static void main(String[] args) {
		SpringApplication.run(HereApplication.class, args);
	}

}

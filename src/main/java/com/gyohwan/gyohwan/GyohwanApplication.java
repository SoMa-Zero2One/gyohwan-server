package com.gyohwan.gyohwan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GyohwanApplication {

    public static void main(String[] args) {
        SpringApplication.run(GyohwanApplication.class, args);
    }

}

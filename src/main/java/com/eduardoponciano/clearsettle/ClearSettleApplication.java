package com.eduardoponciano.clearsettle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.jpa")
@EnableMongoRepositories(basePackages = "com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.mongo")
public class ClearSettleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClearSettleApplication.class, args);
    }
}

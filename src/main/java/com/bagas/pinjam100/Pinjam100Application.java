package com.bagas.pinjam100;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
@EnableMethodSecurity
public class Pinjam100Application {

    public static void main(String[] args) {
        SpringApplication.run(Pinjam100Application.class, args);
    }

}

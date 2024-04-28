package org.example.projecttestassignment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        log.info("Creating Clock bean with system default zone");
        return Clock.systemDefaultZone();
    }
}

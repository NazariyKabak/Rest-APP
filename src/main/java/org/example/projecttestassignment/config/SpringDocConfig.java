package org.example.projecttestassignment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Creating custom OpenAPI configuration");
        return new OpenAPI()
                .info(new Info().title("API Title").version("1.0").description("API Description"));

    }
}

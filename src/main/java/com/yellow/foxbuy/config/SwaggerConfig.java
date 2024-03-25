package com.yellow.foxbuy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI publicAPI() {
        return new OpenAPI()
                .info(new Info().title("Foxbuy Yellow App")
                        .description("API for E-Shop")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}

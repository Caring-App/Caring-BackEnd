package com.caring.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI caringOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Caring API")
                        .description("Caring 플랫폼 API 명세서")
                        .version("v0.0.1"));
    }
}
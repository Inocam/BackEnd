package com.sparta.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private io.swagger.v3.oas.models.info.Info apiInfo() {
        return new Info()
                .title("workspace API")
                .description("workspace API description")
                .version("v1.0")
                .contact(new Contact()
                        .name("API 제공자 이름")
                        .email("api.provider@example.com")
                        .url("http://example.com/contact"));
    }
}

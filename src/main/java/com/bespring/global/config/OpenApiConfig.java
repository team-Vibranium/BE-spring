package com.bespring.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .components(components())
                .addSecurityItem(securityRequirement());
    }

    private Info apiInfo() {
        return new Info()
                .title("AningCall API")
                .description("AningCall 알람 앱의 REST API 명세서")
                .version("1.0.0")
                .contact(new Contact()
                        .name("AningCall Team")
                        .email("contact@aningcall.com")
                        .url("https://aningcall.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("https://prod.proproject.my")
                        .description("Production Server"),
                new Server()
                        .url("https://dev.proproject.my")
                        .description("Development Server"),
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server")
        );
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authentication"));
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}
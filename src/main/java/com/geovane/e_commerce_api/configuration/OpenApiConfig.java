package com.geovane.e_commerce_api.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0")
                        .description("E-Commerce API with shopping cart and payment gateway integration.")
                        .contact(new Contact()
                                .name("Geovane")
                                .email("geovanegsfarias@gmail.com"))
                )
                .addTagsItem(new Tag().name("Authentication"))
                .addTagsItem(new Tag().name("Category"))
                .addTagsItem(new Tag().name("Product"))
                .addTagsItem(new Tag().name("Cart"))
                .addTagsItem(new Tag().name("Order"))
                .addTagsItem(new Tag().name("Checkout"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Basic Auth")
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Basic Auth", new SecurityScheme()
                                .name("Basic Auth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic"))
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        ));
    }

}

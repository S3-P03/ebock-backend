package com.ebock.infrastructure.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

@OpenAPIDefinition(
        info = @Info(
                title = "Ebock API",
                version = "1.0.0"
        ),
        security = {
                @SecurityRequirement(name = "SecurityScheme")
        }
)
public class OpenApiConfig extends Application {
}

package com.ebock.infrastructure.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Ebock API",
                version = "1.0.0"
        ),
        security = {
                @SecurityRequirement(name = "Environment"),
                @SecurityRequirement(name = "SecurityScheme")
        }
)
@SecurityScheme(
        securitySchemeName = "Environment",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        apiKeyName = "Environment",
        description = "'ebock' ou 'dark_ebock'"
)
public class OpenApiConfig extends Application {

}
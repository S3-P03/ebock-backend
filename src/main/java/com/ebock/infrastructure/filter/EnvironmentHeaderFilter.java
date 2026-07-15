package com.ebock.infrastructure.filter;

import com.ebock.infrastructure.config.SchemaContextHolder;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

@Provider
public class EnvironmentHeaderFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    SecurityContext securityContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String environment = requestContext.getHeaderString("Environment");

        if (environment == null || environment.trim().isEmpty()) {
            environment = "ebock";
        }

        if(!environment.equals("ebock") && !environment.equals("dark_ebock")){
            throw new ForbiddenException("Invalid environment");
        }

        if (environment.equals("dark_ebock") && !securityContext.isUserInRole("dark")){
            throw new ForbiddenException("You don't have access to [REDACTED]");
        }

        SchemaContextHolder.setEnvironment(environment);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        SchemaContextHolder.clear();
    }
}
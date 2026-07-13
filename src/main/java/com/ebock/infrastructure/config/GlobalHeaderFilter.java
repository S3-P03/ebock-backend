package com.ebock.infrastructure.config;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;

import java.util.Collections;
import java.util.Map;

public class GlobalHeaderFilter implements OASFilter {
    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (openAPI.getPaths() == null || openAPI.getPaths().getPathItems() == null) {
            return;
        }

        Parameter envHeader = OASFactory.createParameter()
                .name("Environment")
                .in(Parameter.In.HEADER)
                .required(false)
                .description("'ebock' ou 'dark_ebock'")
                .schema(OASFactory.createSchema().type(Collections.singletonList(Schema.SchemaType.STRING)));

        for (Map.Entry<String, PathItem> entry : openAPI.getPaths().getPathItems().entrySet()) {
            PathItem pathItem = entry.getValue();
            if (pathItem.getGET() != null) pathItem.getGET().addParameter(envHeader);
            if (pathItem.getPOST() != null) pathItem.getPOST().addParameter(envHeader);
            if (pathItem.getPUT() != null) pathItem.getPUT().addParameter(envHeader);
            if (pathItem.getDELETE() != null) pathItem.getDELETE().addParameter(envHeader);
            if (pathItem.getPATCH() != null) pathItem.getPATCH().addParameter(envHeader);
        }
    }
}
package com.ebock.service;

import com.ebock.business.DeliveryOption;
import com.ebock.converter.DeliveryOptionConverter;
import com.ebock.dto.response.deliveryOption.DeliveryOptionResponse;
import com.ebock.mapper.DeliveryOptionMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/deliveryOption")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryOptionService {
    @Inject
    DeliveryOptionMapper deliveryMapper;
    @Inject
    DeliveryOptionConverter deliveryConverter;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/list/")
    @PermitAll
    public List<DeliveryOptionResponse> list() {
        List<DeliveryOption> deliveryOptions = this.deliveryMapper.getAllDeliveryOptions();
        return deliveryConverter.toResponse(deliveryOptions);
    }
}

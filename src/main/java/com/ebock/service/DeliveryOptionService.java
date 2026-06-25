package com.ebock.service;

import com.ebock.business.DeliveryOption;
import com.ebock.converter.DeliveryOptionConverter;
import com.ebock.dto.request.deliveryOption.DeliveryOptionPayload;
import com.ebock.dto.response.deliveryOption.DeliveryOptionResponse;
import com.ebock.mapper.DeliveryOptionMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    DeliveryOptionMapper deliveryOptionMapper;
    @Inject
    DeliveryOptionConverter deliveryOptionConverter;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/list/")
    @PermitAll
    public List<DeliveryOptionResponse> list() {
        List<DeliveryOption> deliveryOptions = this.deliveryOptionMapper.getAllDeliveryOptions();
        return deliveryOptionConverter.toResponse(deliveryOptions);
    }

    @POST
    @Path("/insert")
    @Authenticated
    public DeliveryOptionResponse insert(@Valid DeliveryOptionPayload payload) {
        DeliveryOption deliveryOption = deliveryOptionConverter.toBusiness(payload);
        this.deliveryOptionMapper.insert(deliveryOption);
        return deliveryOptionConverter.toResponse(deliveryOption);
    }

    @PUT
    @Path("/update/{id}")
    @Authenticated
    public DeliveryOptionResponse update(@PathParam("id") int id, @Valid DeliveryOptionPayload payload) {
        DeliveryOption deliveryOption = deliveryOptionConverter.toBusiness(payload);
        deliveryOption.deliveryOptnId = id;
        this.deliveryOptionMapper.update(deliveryOption);
        return deliveryOptionConverter.toResponse(deliveryOption);
    }

}

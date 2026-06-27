package com.ebock.service;

import com.ebock.business.PaymentOption;
import com.ebock.converter.PaymentOptionConverter;
import com.ebock.dto.request.paymentOption.PaymentOptionPayload;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import com.ebock.mapper.PaymentOptionMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/paymentOption")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentOptionService {
    @Inject
    PaymentOptionMapper paymentOptionMapper;
    @Inject
    PaymentOptionConverter paymentOptionConverter;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/list/")
    @PermitAll
    public List<PaymentOptionResponse> list() {
        List<PaymentOption> paymentOptions = this.paymentOptionMapper.getAllPaymentOptions();
        return paymentOptionConverter.toResponse(paymentOptions);
    }

    @POST
    @Path("/insert")
    @Authenticated
    public PaymentOptionResponse insert(@Valid PaymentOptionPayload payload) {
        PaymentOption paymentOption = paymentOptionConverter.toBusiness(payload);
        this.paymentOptionMapper.insert(paymentOption);
        return paymentOptionConverter.toResponse(paymentOption);
    }

    @PUT
    @Path("/update/{id}")
    @Authenticated
    public PaymentOptionResponse update(@PathParam("id") int id, @Valid PaymentOptionPayload payload) {
        PaymentOption paymentOption = paymentOptionConverter.toBusiness(payload);
        paymentOption.paymentOptnId = id;
        this.paymentOptionMapper.update(paymentOption);
        return paymentOptionConverter.toResponse(paymentOption);
    }

}

package com.ebock.service;

import com.ebock.business.PaymentOption;
import com.ebock.converter.PaymentOptionConverter;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import com.ebock.mapper.PaymentOptionMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/paymentOption")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentOptionService {
    @Inject
    PaymentOptionMapper paymentMapper;
    @Inject
    PaymentOptionConverter paymentConverter;

    @GET
    @Path("/list/")
    @PermitAll
    public List<PaymentOptionResponse> list() {
        List<PaymentOption> paymentOptions = this.paymentMapper.getAllPaymentOptions();
        return paymentConverter.toResponse(paymentOptions);
    }
}

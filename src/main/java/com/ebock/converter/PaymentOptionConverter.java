package com.ebock.converter;

import com.ebock.business.PaymentOption;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PaymentOptionConverter {
    List<PaymentOptionResponse> toResponse(List<PaymentOption> paymentOptions);
}


package com.ebock.converter;

import com.ebock.business.PaymentOption;
import com.ebock.dto.request.paymentOption.PaymentOptionPayload;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PaymentOptionConverter {
    PaymentOptionResponse toResponse(PaymentOption paymentOption);
    List<PaymentOptionResponse> toResponse(List<PaymentOption> paymentOptions);
    PaymentOption toBusiness(PaymentOptionPayload payload);
}


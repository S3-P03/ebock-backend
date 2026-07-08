package com.ebock.converter;

import com.ebock.business.PaymentOption;
import com.ebock.dto.request.paymentOption.PaymentOptionPayload;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PaymentOptionConverter {
    PaymentOptionResponse toResponse(PaymentOption paymentOption);
    List<PaymentOptionResponse> toResponse(List<PaymentOption> paymentOptions);

    @Mapping(target = "paymentOptnId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    PaymentOption toBusiness(PaymentOptionPayload payload);
}


package com.ebock.converter;

import com.ebock.business.DeliveryOption;
import com.ebock.dto.request.deliveryOption.DeliveryOptionPayload;
import com.ebock.dto.response.deliveryOption.DeliveryOptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface DeliveryOptionConverter {
    DeliveryOptionResponse toResponse(DeliveryOption deliveryOption);
    List<DeliveryOptionResponse> toResponse(List<DeliveryOption> deliveryOptions);

    @Mapping(target = "deliveryOptnId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DeliveryOption toBusiness(DeliveryOptionPayload payload);
}


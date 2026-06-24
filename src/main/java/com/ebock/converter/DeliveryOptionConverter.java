package com.ebock.converter;

import com.ebock.business.DeliveryOption;
import com.ebock.dto.response.deliveryOption.DeliveryOptionResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface DeliveryOptionConverter {
    List<DeliveryOptionResponse> toResponse(List<DeliveryOption> deliveryOptions);
}


package com.ebock.dto.request.deliveryOption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DeliveryOptionPayload {
    @NotBlank
    @Size(max=50)
    public String name;
}

package com.ebock.dto.request.paymentOption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PaymentOptionPayload {
    @NotBlank
    @Size(max=50)
    public String name;
}

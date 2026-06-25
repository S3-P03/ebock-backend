package com.ebock.dto.request.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class EditPayload {
    @Valid
    public EditUserPayload user;

    @Valid
    public EditAddressPayload address;
}
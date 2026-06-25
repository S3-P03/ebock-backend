package com.ebock.dto.request.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserEditPayload {
    @Size(max=100)
    public String firstName;

    @Size(max=100)
    public String lastName;

    @Valid
    public AddressPayload address;
}
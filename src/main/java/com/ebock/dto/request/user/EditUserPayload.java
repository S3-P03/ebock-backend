package com.ebock.dto.request.user;

import jakarta.validation.constraints.Size;

public class EditUserPayload {
    @Size(max=100)
    public String firstName;

    @Size(max=100)
    public String lastName;
}

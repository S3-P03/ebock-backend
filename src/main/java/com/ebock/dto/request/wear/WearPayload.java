package com.ebock.dto.request.wear;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WearPayload {
    @NotBlank
    @Size(max=50)
    public String name;
}

package com.ebock.dto.request.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagPayload {
    @NotBlank
    @Size(max=50)
    public String name;
}

package com.ebock.dto.request.message;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoomPayload {
    @Min(1)
    public int itemId;
    @NotBlank
    @Size(min=8, max=8)
    public String buyerCip;
}

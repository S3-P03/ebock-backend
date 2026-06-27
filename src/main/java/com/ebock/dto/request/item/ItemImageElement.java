package com.ebock.dto.request.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ItemImageElement {
    @NotBlank
    @Size(min=32, max=36)
    public String guid;
    @Min(value=1)
    public int displayorder;
}

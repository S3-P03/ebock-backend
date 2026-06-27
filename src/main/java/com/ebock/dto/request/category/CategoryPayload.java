package com.ebock.dto.request.category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryPayload {
    @NotBlank
    @Size(max=50)
    public String name;
    @Min(1)
    public Integer parentCategory;
}

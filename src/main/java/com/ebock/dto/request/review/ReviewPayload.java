package com.ebock.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReviewPayload {
    @NotBlank
    @Size(max=50)
    public String content;
    @Min(1)
    @Max(5)
    public int rating;
}

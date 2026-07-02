package com.ebock.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ReviewPayload {
    public String content;
    @Min(1)
    @Max(5)
    public int rating;
}

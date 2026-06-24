package com.ebock.converter;

import com.ebock.business.Review;
import com.ebock.dto.response.review.ReviewDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ReviewConverter {
    ReviewDetailsResponse toResponse(Review review);
}

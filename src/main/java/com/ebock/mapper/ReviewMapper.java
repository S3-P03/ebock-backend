package com.ebock.mapper;

import com.ebock.dto.response.review.AverageReviewResponse;
import com.ebock.dto.response.review.ReviewDetailsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    AverageReviewResponse getUserAverageReview(@Param("cip") String cip);
    List<ReviewDetailsResponse> getDetailledReviews(@Param("cip") String cip);
}

package com.ebock.mapper;

import com.ebock.dto.request.review.ReviewPayload;
import com.ebock.dto.response.review.AverageReviewResponse;
import com.ebock.dto.response.review.ReviewDetailsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    AverageReviewResponse getUserAverageReview(@Param("cip") String cip);
    List<ReviewDetailsResponse> getDetailledReviews(@Param("cip") String cip);
    void insert(@Param("reviewerCip") String reviewerCip,
                      @Param("reviewedCip") String reviewedCip,
                      @Param("review")ReviewPayload review);
    void update(@Param("reviewerCip") String reviewerCip,
                @Param("reviewedCip") String reviewedCip,
                @Param("review")ReviewPayload review);
    int getCountByUsers(@Param("reviewerCip") String reviewerCip,
                        @Param("reviewedCip") String reviewedCip);
}

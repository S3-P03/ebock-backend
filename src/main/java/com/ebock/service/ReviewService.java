package com.ebock.service;

import com.ebock.dto.response.review.AverageReviewResponse;
import com.ebock.dto.response.review.ReviewDetailsResponse;
import com.ebock.mapper.ReviewMapper;
import com.ebock.mapper.UserMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewService {
    @Inject
    ReviewMapper reviewMapper;

    @Inject
    UserMapper userMapper;

    @GET
    @Path("/{cip}/average")
    @PermitAll
    public AverageReviewResponse cipAverageReview(@PathParam("cip") String cip) {
        if (userMapper.getUserCountByCip(cip) == 0)
            throw new NotFoundException("User not found");

        AverageReviewResponse averageReviewResponse = reviewMapper.getUserAverageReview(cip);
        if (averageReviewResponse == null){
            averageReviewResponse = new AverageReviewResponse();
            averageReviewResponse.avgRating = 0;
            averageReviewResponse.nbrReviews = 0;
        }
        return averageReviewResponse;
    }

    @GET
    @Path("/{cip}/details")
    @PermitAll
    public List<ReviewDetailsResponse> cipDetailsReview(@PathParam("cip") String cip) {
        if (userMapper.getUserCountByCip(cip) == 0)
            throw new NotFoundException("User not found");

        return reviewMapper.getDetailledReviews(cip);
    }
}

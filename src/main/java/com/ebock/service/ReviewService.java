package com.ebock.service;

import com.ebock.dto.request.review.ReviewPayload;
import com.ebock.dto.response.review.AverageReviewResponse;
import com.ebock.dto.response.review.ReviewDetailsResponse;
import com.ebock.mapper.MessageMapper;
import com.ebock.mapper.ReviewMapper;
import com.ebock.mapper.UserMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewService {
    @Inject
    ReviewMapper reviewMapper;
    @Inject
    UserMapper userMapper;
    @Inject
    MessageMapper messageMapper;
    @Context
    SecurityContext securityContext;

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

    @POST
    @Path("/{cip}")
    @Authenticated
    public Response insert(@PathParam("cip") String reviewedCip, @Valid ReviewPayload reviewPayload){
        String reviewerCip = securityContext.getUserPrincipal().getName();

        if (userMapper.getUserCountByCip(reviewedCip) == 0)
            return Response.status(Response.Status.NOT_FOUND).build();
        if(messageMapper.getMessagesFromSellerCountByIds(reviewerCip, reviewedCip) == 0)
            return Response.status(Response.Status.FORBIDDEN).build();

        reviewMapper.insert(reviewerCip, reviewedCip, reviewPayload);

        return Response.status(Response.Status.OK).build();
    }
}

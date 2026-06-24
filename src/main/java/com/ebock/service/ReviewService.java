package com.ebock.service;

import com.ebock.dto.response.review.AverageReviewResponse;
import com.ebock.dto.response.review.ReviewDetailsResponse;
import com.ebock.mapper.ReviewMapper;
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

    @GET
    @Path("/{cip}/average")
    @PermitAll
    public AverageReviewResponse cipAverageReview(@PathParam("cip") String cip){
        //TODO faire service (gérer mauvais cip aussi)
    }

    @GET
    @Path("/{cip}/details")
    @PermitAll
    public List<ReviewDetailsResponse> cipDetailsReview(@PathParam("cip") String cip){
        //TODO faire service (gérer mauvais cip aussi)
    }

    //TODO faire le fichier mapper.xml et faire les requêtes sql
}

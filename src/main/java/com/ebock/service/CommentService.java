package com.ebock.service;

import com.ebock.dto.request.comment.CommentPayload;
import com.ebock.dto.response.comment.CommentDetailsResponse;
import com.ebock.mapper.CommentMapper;
import com.ebock.mapper.ItemMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/comment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentService {

    @Inject
    CommentMapper commentMapper;
    @Inject
    ItemMapper itemMapper;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/{id}/details")
    @PermitAll
    public List<CommentDetailsResponse> idDetailsComment(@PathParam("id") Integer id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");
        return commentMapper.getDetailledComments(id);
    }

    @POST
    @Path("/{id}")
    @Authenticated
    public Response insert(@PathParam("id") Integer id, @Valid CommentPayload commentPayload){
        String cip = securityContext.getUserPrincipal().getName();

        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");

        commentMapper.insert(id, cip, commentPayload);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") Integer id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");
        commentMapper.delete(id);
        return Response.noContent().build();
    }
}

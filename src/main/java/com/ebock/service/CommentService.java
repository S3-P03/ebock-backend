package com.ebock.service;

import com.ebock.mapper.CommentMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/comment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentService {

    @Inject
    CommentMapper commentMapper;

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") Integer id) {
        if (commentMapper.getCountById(id) == 0)
            throw new NotFoundException("Comment not found");
        commentMapper.delete(id);
        return Response.noContent().build();
    }
}

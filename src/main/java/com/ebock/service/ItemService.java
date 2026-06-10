package com.ebock.service;

import com.ebock.converter.ItemConverter;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.UserMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemService {
    @Inject
    ItemMapper itemMapper;

    @Inject
    UserMapper userMapper;

    @GET
    @Path("/list/{pageNumber}")
    @PermitAll
    public List<ItemResponse> list(@PathParam("pageNumber") int pageNumber) {
        int pageSize = 25;

        if (pageNumber < 1) {
            throw new BadRequestException("pageNumber must be >= 1");
        }

        return this.itemMapper.getPaginatedItem(pageNumber, pageSize);
    }

    @GET
    @Path("/{cip}/storefront")
    @PermitAll
    public List<ItemResponse> cipStorefront(
            @PathParam("cip") String cip
    ) {
        if(userMapper.findUserByCip(cip) == 0)
            throw new NotFoundException("User not found");
        return this.itemMapper.getAllItemsSeller(cip);
    }

    @GET
    @Path("/{cip}/storefront")
    @Authenticated
    public List<ItemResponse> cipStorefront(
            @PathParam("cip") String cip
    ) {
        return this.itemMapper.getAllItemsSeller(cip);
    }
}

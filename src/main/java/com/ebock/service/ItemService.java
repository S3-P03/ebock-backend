package com.ebock.service;

import com.ebock.converter.ItemConverter;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.ItemMapper;
import io.quarkus.security.Authenticated;
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
    @Context
    SecurityContext securityContext;
    @Inject
    JsonWebToken jwt;
    @Inject
    ItemConverter itemConverter;

    @GET
    @Path("/list")
    @Authenticated
    public List<ItemResponse> list() {
        return this.itemMapper.getAllItemsInfo();
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

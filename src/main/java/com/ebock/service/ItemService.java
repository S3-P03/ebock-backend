package com.ebock.service;

import com.ebock.business.Item;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.response.ItemResponse;
import com.ebock.mapper.ItemMapper;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
}

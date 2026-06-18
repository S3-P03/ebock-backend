package com.ebock.service;

import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.request.item.FilterItemPayload;
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
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import java.util.List;

@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemService {
    @Inject
    ItemMapper itemMapper;

    @Inject
    UserMapper userMapper;

    @Context
    SecurityContext securityContext;

    @POST
    @Path("/list/{pageNumber}")
    @PermitAll
    //@SecurityRequirement(name = "SecurityScheme")
    public List<ItemResponse> list(@PathParam("pageNumber") int pageNumber, FilterItemPayload filterItemPayload) {
        int pageSize = 25;

        if (pageNumber < 1) {
            throw new BadRequestException("pageNumber must be >= 1");
        }
        String cip = "";
        try{
            cip = securityContext.getUserPrincipal().getName();
        } catch (Exception e){}

        System.out.println("*" + cip + "*");

        return this.itemMapper.getPaginatedItem(pageNumber, pageSize, filterItemPayload, cip);
    }

    @GET
    @Path("/{cip}/storefront")
    @PermitAll
    public List<ItemResponse> cipStorefront(
            @PathParam("cip") String cip
    ) {
        if (userMapper.findUserByCip(cip) == 0)
            throw new NotFoundException("User not found");
        return this.itemMapper.getAllItemsSeller(cip);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public ItemDetailsResponse itemDetails(@PathParam("id") int id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");
        return this.itemMapper.getItemDetails(id);
    }


}

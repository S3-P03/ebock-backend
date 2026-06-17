package com.ebock.service;

import com.ebock.business.Item;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.payload.item.ItemInsertPayload;
import com.ebock.dto.response.item.ItemInsertResponse;
import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.ItemImageMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.ItemTagMapper;
import com.ebock.mapper.UserMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemService {
    @Inject
    ItemMapper itemMapper;
    @Inject
    ItemImageMapper itemImageMapper;
    @Inject
    ItemTagMapper itemTagMapper;
    @Inject
    UserMapper userMapper;
    @Inject
    ItemConverter itemConverter;
    @Context
    SecurityContext securityContext;

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
    @Path("/{id}")
    @PermitAll
    public ItemDetailsResponse itemDetails(@PathParam("id") int id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");
        return this.itemMapper.getItemDetails(id);
    }

    @POST
    @Path("/insert")
    @Authenticated
    public ItemInsertResponse insert(ItemInsertPayload itemInsertPayload){
        Item item = itemConverter.toBusiness(itemInsertPayload);
        String cip = this.securityContext.getUserPrincipal().getName();
        item.sellerCip = cip;

        itemMapper.insert(item);

        if (itemInsertPayload.tagList != null && !itemInsertPayload.tagList.isEmpty()) {
            itemTagMapper.insert(item.itemId, itemInsertPayload.tagList);
        }

        if(itemInsertPayload.imageList != null && !itemInsertPayload.imageList.isEmpty()){
            itemImageMapper.insert(item.itemId, itemInsertPayload.imageList);
        }

        return itemConverter.toInsertResponse(item);
    }
}

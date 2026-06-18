package com.ebock.service;

import com.ebock.business.Item;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.request.item.ItemPayload;
import com.ebock.dto.response.item.ItemInsertResponse;
import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.request.item.FilterItemPayload;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.ItemImageMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.ItemTagMapper;
import com.ebock.mapper.UserMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import java.util.List;
import java.util.Objects;

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

    @POST
    @Path("/insert")
    @Authenticated
    @Transactional
    public ItemInsertResponse insert(@Valid ItemPayload itemInsertPayload){
        Item item = itemConverter.toBusiness(itemInsertPayload);
        String cip = securityContext.getUserPrincipal().getName();
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

    @PUT
    @Path("/update/{id}")
    @Authenticated
    @Transactional
    public ItemInsertResponse update(@PathParam("id") int itemId, @Valid ItemPayload itemInsertPayload){
        Item item = itemConverter.toBusiness(itemInsertPayload);
        item.itemId = itemId;

        String cip = securityContext.getUserPrincipal().getName();
        Item existingItem = itemMapper.findById(itemId);

        if(existingItem == null){
            throw new NotFoundException("Item not found");
        }

        if(!Objects.equals(existingItem.sellerCip, cip)){
            throw new ForbiddenException("Not your item");
        }

        itemMapper.update(cip, item);

        // Update tags
        itemTagMapper.deleteByItemId(itemId);
        if (itemInsertPayload.tagList != null && !itemInsertPayload.tagList.isEmpty()) {
            itemTagMapper.insert(item.itemId, itemInsertPayload.tagList);
        }

        // Update images
        itemImageMapper.deleteByItemId(itemId);
        if(itemInsertPayload.imageList != null && !itemInsertPayload.imageList.isEmpty()){
            itemImageMapper.insert(item.itemId, itemInsertPayload.imageList);
        }

        return itemConverter.toInsertResponse(item);
    }
}

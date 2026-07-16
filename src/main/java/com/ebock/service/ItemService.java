package com.ebock.service;

import com.ebock.business.Item;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.request.comment.CommentPayload;
import com.ebock.dto.request.item.FilterItemParameters;
import com.ebock.dto.request.item.ItemCreatePayload;
import com.ebock.dto.request.item.ItemUpdatePayload;
import com.ebock.dto.response.comment.CommentDetailsResponse;
import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.dto.response.item.ItemInsertResponse;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.*;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.Separator;

import java.math.BigDecimal;
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
    ItemPaymentOptionMapper itemPaymentOptionMapper;
    @Inject
    ItemDeliveryOptionMapper itemDeliveryOptionMapper;
    @Inject
    ItemTagMapper itemTagMapper;
    @Inject
    UserMapper userMapper;
    @Inject
    ItemConverter itemConverter;
    @Inject
    CommentMapper commentMapper;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/list/{pageNumber}")
    @PermitAll
    //@SecurityRequirement(name = "SecurityScheme")
    public List<ItemResponse> list(@PathParam("pageNumber") int pageNumber,
                                   @RestQuery BigDecimal minP,
                                   @RestQuery BigDecimal maxP,
                                   @RestQuery Integer maxD,
                                   @RestQuery Boolean fav,
                                   @RestQuery @Separator(",") List<Integer> categories,
                                   @RestQuery @Separator(",") List<Integer> tags,
                                   @RestQuery @Separator(",") List<Integer> wears,
                                   @RestQuery @Separator(",") List<Integer> deliveries,
                                   @RestQuery @Separator(",") List<Integer> payments) {
        FilterItemParameters filterItemParameters = new FilterItemParameters();
        filterItemParameters.minPrice = minP;
        filterItemParameters.maxPrice = maxP;
        filterItemParameters.maxDistance = maxD;
        filterItemParameters.favorite = fav;
        filterItemParameters.listCategoryId = categories;
        filterItemParameters.listTagId = tags;
        filterItemParameters.listWearId = wears;
        filterItemParameters.listDeliveryId = deliveries;
        filterItemParameters.listPaymentId = payments;
        int pageSize = 25;

        if (pageNumber < 1) {
            throw new BadRequestException("pageNumber must be >= 1");
        }
        String cip = "";
        try{
            cip = securityContext.getUserPrincipal().getName();
        } catch (Exception e){}

        return this.itemMapper.getPaginatedItem(pageNumber, pageSize, filterItemParameters, cip);
    }

    @GET
    @Path("/{cip}/storefront")
    @PermitAll
    public List<ItemResponse> cipStorefront(
            @PathParam("cip") String cip
    ) {
        if(userMapper.getUserCountByCip(cip) == 0)
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
    @Path("/{id}/favorite")
    @Authenticated
    public Response addFavorite(@PathParam("id") int id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");
        String cip = securityContext.getUserPrincipal().getName();
        if(userMapper.getUserCountByCip(cip) == 0)
            throw new UnauthorizedException("User not authorized");
        itemMapper.favorite(id, cip);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/{id}/unfavorite")
    @Authenticated
    public Response removeFavorite(@PathParam("id") int id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");
        String cip = securityContext.getUserPrincipal().getName();
        if(userMapper.getUserCountByCip(cip) == 0)
            throw new UnauthorizedException("User not authorized");
        itemMapper.unfavorite(id, cip);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("")
    @Authenticated
    @Transactional
    public ItemInsertResponse insert(@Valid ItemCreatePayload itemInsertPayload){
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

        if(itemInsertPayload.paymentOptionList != null && !itemInsertPayload.paymentOptionList.isEmpty()){
            itemPaymentOptionMapper.insert(item.itemId, itemInsertPayload.paymentOptionList);
        }

        if(itemInsertPayload.deliveryOptionList != null && !itemInsertPayload.deliveryOptionList.isEmpty()){
            itemDeliveryOptionMapper.insert(item.itemId, itemInsertPayload.deliveryOptionList);
        }

        return itemConverter.toInsertResponse(item);
    }

    @PUT
    @Path("/{id}")
    @Authenticated
    @Transactional
    public Response update(@PathParam("id") int itemId, @Valid ItemUpdatePayload itemUpdatePayload){
        Item item = itemConverter.toBusiness(itemUpdatePayload);
        item.itemId = itemId;

        String cip = securityContext.getUserPrincipal().getName();
        Item existingItem = itemMapper.findById(itemId);

        if(existingItem == null){
            throw new NotFoundException("Item not found");
        }

        if(!Objects.equals(existingItem.sellerCip, cip)){
            throw new ForbiddenException("Not your item");
        }

        if(existingItem.quantity == 0) {
            throw new ForbiddenException("Cannot modify item out of stock");
        }

        if (item.quantity == 0) {
            itemMapper.archiveRoomsById(item.itemId);
        }

        itemMapper.update(cip, item);

        // Update tags
        if (itemUpdatePayload.tagList != null) {
            itemTagMapper.deleteByItemId(itemId);
            if (!itemUpdatePayload.tagList.isEmpty()) {
                itemTagMapper.insert(item.itemId, itemUpdatePayload.tagList);
            }
        }

        // Update images
        if (itemUpdatePayload.imageList != null) {
            itemImageMapper.deleteByItemId(itemId);
            if (!itemUpdatePayload.imageList.isEmpty()) {
                itemImageMapper.insert(item.itemId, itemUpdatePayload.imageList);
            }
        }

        // Update payment option
        if (itemUpdatePayload.paymentOptionList != null) {
            itemPaymentOptionMapper.deleteByItemId(itemId);
            if (!itemUpdatePayload.paymentOptionList.isEmpty()) {
                itemPaymentOptionMapper.insert(item.itemId, itemUpdatePayload.paymentOptionList);
            }
        }

        // Update delivery option
        if (itemUpdatePayload.deliveryOptionList != null) {
            itemDeliveryOptionMapper.deleteByItemId(itemId);
            if (!itemUpdatePayload.deliveryOptionList.isEmpty()) {
                itemDeliveryOptionMapper.insert(item.itemId, itemUpdatePayload.deliveryOptionList);
            }
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/comment")
    @PermitAll
    public List<CommentDetailsResponse> listItemComments(@PathParam("id") Integer id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");

        return commentMapper.getDetailledComments(id);
    }

    @POST
    @Path("/{id}/comment")
    @Authenticated
    public Response insertComment(@PathParam("id") Integer id, @Valid CommentPayload commentPayload){
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");

        String cip = securityContext.getUserPrincipal().getName();

        commentMapper.insert(id, cip, commentPayload);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") int id){
        itemMapper.delete(id);
        int rowsAffected = itemMapper.archiveRoomsById(id);

        if(rowsAffected == 0 && itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");
        return Response.noContent().build();
    }
}

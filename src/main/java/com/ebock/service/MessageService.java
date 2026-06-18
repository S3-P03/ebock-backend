package com.ebock.service;

import com.ebock.dto.request.message.MessagePayload;
import com.ebock.dto.request.message.RoomPayload;
import com.ebock.dto.response.message.MessageResponse;
import com.ebock.dto.response.message.RoomDetailsResponse;
import com.ebock.dto.response.message.RoomResponse;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.MessageMapper;
import com.ebock.mapper.UserMapper;
import com.ebock.websocket.MessageBroadcaster;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MessageService {

    @Inject
    MessageMapper messageMapper;

    @Inject
    UserMapper userMapper;

    @Inject
    ItemMapper itemMapper;

    @Context
    SecurityContext securityContext;

    @Inject
    MessageBroadcaster messageBroadcaster;

    @GET
    @Path("/room/{id}")
    @Authenticated
    public RoomDetailsResponse getRoomInformation(@PathParam("id") int id) {
        String cip = this.securityContext.getUserPrincipal().getName();
        if(userMapper.getUserCountByCip(cip) == 0)
            throw new NotFoundException("Connected user not found");
        if(messageMapper.getRoomCountById(id) == 0)
            throw new NotFoundException("Room not found");
        RoomDetailsResponse roomResponse = this.messageMapper.getRoomInformation(id);

        // Check if one of the 2 users in the room is the one making the request
        if (!roomResponse.sellerCip.equals(cip) && !roomResponse.buyerCip.equals(cip))
            throw new UnauthorizedException("Access not authorized");

        return roomResponse;
    }

    @GET
    @Path("/room/{id}/messages")
    @Authenticated
    public List<MessageResponse> listRoomMessages(@PathParam("id") int id) {
        String cip = this.securityContext.getUserPrincipal().getName();
        if(userMapper.getUserCountByCip(cip) == 0)
            throw new NotFoundException("Connected user not found");
        if(messageMapper.getRoomCountById(id) == 0)
            throw new NotFoundException("Room not found");
        RoomDetailsResponse roomResponse = this.messageMapper.getRoomInformation(id);

        // Check if one of the 2 users in the room is the one making the request
        if (!roomResponse.sellerCip.equals(cip) && !roomResponse.buyerCip.equals(cip))
            throw new UnauthorizedException("Access not authorized");

        return this.messageMapper.getAllRoomMessages(id);
    }

    @POST
    @Path("/room")
    @Authenticated
    public RoomResponse post(RoomPayload room) {
        String cip = this.securityContext.getUserPrincipal().getName();
        if(userMapper.getUserCountByCip(cip) == 0 || !cip.equals(room.buyerCip))
            throw new NotFoundException("Connected user not found");
        if(itemMapper.getItemCountById(room.itemId) == 0)
            throw new NotFoundException("Item not fonud");
        return messageMapper.createRoom(room.itemId, cip);
    }

    @POST
    @Path("/room/{id}")
    @Transactional
    @Authenticated
    public MessageResponse post(MessagePayload message, @PathParam("id") int id) {
        String cip = this.securityContext.getUserPrincipal().getName();
        if(userMapper.getUserCountByCip(cip) == 0)
            throw new NotFoundException("Connected user not found");
        if(messageMapper.getRoomCountById(id) == 0)
            throw new NotFoundException("Room not found");
        RoomDetailsResponse roomResponse = this.messageMapper.getRoomInformation(id);

        // Check if one of the 2 users in the room is the one connected
        if (!roomResponse.sellerCip.equals(cip) && !roomResponse.buyerCip.equals(cip))
            throw new UnauthorizedException("Access not authorized");

        MessageResponse saved = messageMapper.insert(message.content, cip, id);
        messageBroadcaster.broadcast(saved);
        return saved;
    }
}

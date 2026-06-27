package com.ebock.message;

import com.ebock.dto.request.message.MessagePayload;
import com.ebock.dto.request.message.RoomPayload;
import com.ebock.dto.response.message.MessageResponse;
import com.ebock.dto.response.message.RoomDetailsResponse;
import com.ebock.dto.response.message.RoomResponse;
import com.ebock.mapper.*;
import com.ebock.service.MessageService;
import com.ebock.websocket.MessageBroadcaster;
import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    ItemMapper itemMapper;
    @Mock
    MessageMapper messageMapper;
    @Mock
    UserMapper userMapper;
    @Mock
    MessageBroadcaster messageBroadcaster;
    @Mock
    SecurityContext securityContext;
    @Mock
    Principal principal;

    @InjectMocks
    MessageService messageService;

    @Test
    void testRoomInfoReturnsRoomInformation() {
        // arrange
        String requestCip = "larj4236";
        RoomDetailsResponse expected = new RoomDetailsResponse();
        expected.sellerCip = requestCip;
        expected.buyerCip = requestCip;

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(1);
        when(messageMapper.getRoomInformation(1)).thenReturn(expected);
        // act
        RoomDetailsResponse result = messageService.getRoomInformation(1);
        // assert
        assert(expected.equals(result));
    }

    @Test
    void testRoomInfoUnknownUserThrowsNotFound() {
        // arrange
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.getRoomInformation(1));
    }

    @Test
    void testRoomInfoUnknownRoomThrowsNotFound() {
        // arrange
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.getRoomInformation(1));
    }

    @Test
    void testRoomInfoUnauthorizedThrowsUnauthorized() {
        // arrange
        String requestCip = "badCip";
        RoomDetailsResponse expected = new RoomDetailsResponse();
        expected.sellerCip = requestCip;
        expected.buyerCip = requestCip;

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(1);
        when(messageMapper.getRoomInformation(1)).thenReturn(expected);
        // act and assert
        assertThrows(UnauthorizedException.class, () -> messageService.getRoomInformation(1));
    }

    @Test
    void testListMessagesReturnsRoomMessages() {
        // arrange
        String requestCip = "larj4236";
        RoomDetailsResponse roomResponse = new RoomDetailsResponse();
        roomResponse.sellerCip = requestCip;
        roomResponse.buyerCip = requestCip;

        List<MessageResponse> expected = new ArrayList<>();

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(1);
        when(messageMapper.getRoomInformation(1)).thenReturn(roomResponse);
        when(messageMapper.getAllRoomMessages(1)).thenReturn(expected);
        // act
        List<MessageResponse> result = messageService.listRoomMessages(1);
        // assert
        assert(expected.equals(result));
    }

    @Test
    void testListMessagesUnknownUserThrowsNotFound() {
        // arrange
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(0);

        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.listRoomMessages(1));
    }

    @Test
    void testListMessagesUnknownRoomThrowsNotFound() {
        // arrange
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.listRoomMessages(1));
    }

    @Test
    void testListMessagesUnauthorizedThrowsUnauthorized() {
        // arrange
        String requestCip = "badCip";
        RoomDetailsResponse roomResponse = new RoomDetailsResponse();
        roomResponse.sellerCip = requestCip;
        roomResponse.buyerCip = requestCip;

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(1);
        when(messageMapper.getRoomInformation(1)).thenReturn(roomResponse);
        // act and assert
        assertThrows(UnauthorizedException.class, () -> messageService.listRoomMessages(1));
    }

    @Test
    void testListUserRoomsReturnsUserRooms() {
        // arrange
        List<RoomDetailsResponse> expected = new ArrayList<>();

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getAllUserRooms("larj4236")).thenReturn(expected);
        // act
        List<RoomDetailsResponse> result = messageService.listUserRooms();
        // assert
        assert(expected.equals(result));
    }

    @Test
    void testListUserRoomsUnknownUserReturnsThrowsNotFound() {
        // arrange
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(0);

        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.listRoomMessages(1));
    }

    @Test
    void testCreateRoomReturnsCreatedRoom() {
        // arrange
        RoomResponse expected = new RoomResponse();
        RoomPayload payload = new RoomPayload();
        payload.buyerCip = "larj4236";
        payload.itemId=1;

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(itemMapper.getItemCountById(1)).thenReturn(1);
        when(messageMapper.createRoom(1, "larj4236")).thenReturn(expected);
        // act
        RoomResponse result = messageService.createRoom(payload);
        // assert
        assert(result.getClass().equals(RoomResponse.class));
    }

    @Test
    void testCreateRoomUnknownUserThrowsNotFound() {
        // arrange
        RoomPayload payload = new RoomPayload();

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.createRoom(payload));
    }

    @Test
    void testCreateRoomUnknownItemThrowsNotFound() {
        // arrange
        RoomPayload payload = new RoomPayload();
        payload.buyerCip = "larj4236";
        payload.itemId=1;

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(itemMapper.getItemCountById(1)).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.createRoom(payload));
    }

    @Test
    void testPostMessageReturnsSavedMessage() {
        // arrange
        String requestCip = "larj4236";
        RoomDetailsResponse roomResponse = new RoomDetailsResponse();
        roomResponse.sellerCip = requestCip;
        roomResponse.buyerCip = requestCip;
        MessageResponse expected = new MessageResponse();
        MessagePayload payload = new MessagePayload();
        payload.content = "Test";

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(1);
        when(messageMapper.getRoomInformation(1)).thenReturn(roomResponse);
        when(messageMapper.insert("Test", "larj4236", 1)).thenReturn(expected);
        // act
        MessageResponse result = messageService.postMessage(payload, 1);
        // assert
        assert(result.getClass().equals(MessageResponse.class));
    }

    @Test
    void testPostMessageUnknownUserThrowsNotFound() {
        // arrange
        MessagePayload payload = new MessagePayload();

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.postMessage(payload, 1));
    }

    @Test
    void testPostMessageUnknownRoomThrowsNotFound() {
        // arrange
        MessagePayload payload = new MessagePayload();

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> messageService.postMessage(payload, 1));
    }

    @Test
    void testPostMessageUnauthorizedThrowsUnauthorized() {
        // arrange
        String requestCip = "badCip";
        RoomDetailsResponse roomResponse = new RoomDetailsResponse();
        roomResponse.sellerCip = requestCip;
        roomResponse.buyerCip = requestCip;
        MessageResponse expected = new MessageResponse();
        MessagePayload payload = new MessagePayload();

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(messageMapper.getRoomCountById(1)).thenReturn(1);
        when(messageMapper.getRoomInformation(1)).thenReturn(roomResponse);
        // act and assert
        assertThrows(UnauthorizedException.class, () -> messageService.postMessage(payload, 1));
    }
}

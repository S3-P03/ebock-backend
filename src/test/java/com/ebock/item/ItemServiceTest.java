package com.ebock.item;

import com.ebock.business.Item;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.request.item.FilterItemParameters;
import com.ebock.dto.request.item.ItemImageElement;
import com.ebock.dto.request.item.ItemPayload;
import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.ItemImageMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.ItemTagMapper;
import com.ebock.mapper.UserMapper;
import com.ebock.mapper.*;
import com.ebock.service.ItemService;
import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    ItemMapper itemMapper;
    @Mock
    ItemImageMapper itemImageMapper;
    @Mock
    ItemPaymentOptionMapper itemPaymentOptionMapper;
    @Mock
    ItemDeliveryOptionMapper itemDeliveryOptionMapper;
    @Mock
    ItemTagMapper itemTagMapper;
    @Mock
    ItemConverter itemConverter;
    @Mock
    UserMapper userMapper;
    @Mock
    SecurityContext securityContext;
    @Mock
    Principal principal;

    @InjectMocks
    ItemService itemService;

    @Test
    void testListReturnsListOfItems() {
        // arrange
        String requestCip = "pele3157";
        List<ItemResponse> expected = new ArrayList<>();
        when(itemMapper.getPaginatedItem(eq(1), eq(25), any(FilterItemParameters.class), eq(requestCip))).thenReturn(expected);

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // act
        List<ItemResponse> result = itemService.list(1, null,null,null,null,null,null,null,null,null);

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testList_ThrowsBadRequest_WhenNegativePageNumber(){
        //arrange
        //act and assert
        assertThrows(BadRequestException.class, () -> {
            itemService.list(-1,  null,null,null,null,null,null,null,null,null);
        });
    }

    @Test
    void testList_Works_WhenPayloadNoFilters(){
        // arrange
        String requestCip = "pele3157";
        List<ItemResponse> expected = new ArrayList<>();
        when(itemMapper.getPaginatedItem(eq(1), eq(25), any(FilterItemParameters.class), eq(requestCip))).thenReturn(expected);

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // act
        List<ItemResponse> result = itemService.list(1,  null,null,null,null,null,null,null,null,null);

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testList_Works_WhenNotConnected(){
        //arrange
        List<ItemResponse> expected = new ArrayList<>();
        when(itemMapper.getPaginatedItem(eq(1), eq(25), any(FilterItemParameters.class), eq(""))).thenReturn(expected);

        //act
        List<ItemResponse> result = itemService.list(1,  null,null,null,null,null,null,null,null,null);

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testItemDetailsReturnsItemInformation() {
        // arrange
        ItemDetailsResponse expected = new ItemDetailsResponse();
        when(itemMapper.getItemCountById(1)).thenReturn(1);
        when(itemMapper.getItemDetails(1)).thenReturn(expected);

        // act
        ItemDetailsResponse result = itemService.itemDetails(1);

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testCipStorefrontReturnsAssociatedItems() {
        // arrange
        List<ItemResponse> expected = new ArrayList<>();
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(itemMapper.getAllItemsSeller("larj4236")).thenReturn(expected);

        // act
        List<ItemResponse> result = itemService.cipStorefront("larj4236");

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testCipStorefrontUnknownCipThrowsNotFound() {
        // arrange
        when(userMapper.getUserCountByCip("abcd1234")).thenReturn(0);

        // act and assert
        assertThrows(NotFoundException.class, () -> itemService.cipStorefront("abcd1234"));
    }

    @Test
    void testUpdate_ThrowsForbidden_WhenNotSellerCip(){
        // Arrange
        int itemId = 5;
        String requestCip = "dubw5596";
        String sellerCip = "asdf6767";

        ItemPayload payload = new ItemPayload();
        Item item = new Item();
        item.sellerCip = sellerCip;

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock db return
        when(itemMapper.findById(itemId)).thenReturn(item);
        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act and assert
        assertThrows(ForbiddenException.class, () -> {
            itemService.update(itemId, payload);
        });

        Mockito.verify(itemMapper, Mockito.times(0)).update(anyString(), any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(0)).deleteByItemId(anyInt());
        Mockito.verify(itemImageMapper, Mockito.times(0)).deleteByItemId(anyInt());
    }

    @Test
    void testUpdate_Work_WhenFullPayload(){
        // Arrange
        int itemId = 5;
        String requestCip = "dubw5596";
        String sellerCip = "dubw5596";

        ItemPayload payload = new ItemPayload();
        payload.tagList = List.of(1, 2, 3);
        payload.deliveryOptionList = List.of(4,5,6);
        payload.paymentOptionList = List.of(7,8,9);
        payload.imageList = List.of(new ItemImageElement(), new ItemImageElement());
        Item item = new Item();
        item.sellerCip = sellerCip;

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock db return
        when(itemMapper.findById(itemId)).thenReturn(item);
        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act
        itemService.update(itemId, payload);

        // Assert
        Mockito.verify(itemMapper, Mockito.times(1)).update(anyString(), any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemTagMapper, Mockito.times(1)).insert(anyInt(), anyList());
        Mockito.verify(itemImageMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemImageMapper, Mockito.times(1)).insert(anyInt(), anyList());
        Mockito.verify(itemDeliveryOptionMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemDeliveryOptionMapper, Mockito.times(1)).insert(anyInt(), anyList());
        Mockito.verify(itemPaymentOptionMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemPaymentOptionMapper, Mockito.times(1)).insert(anyInt(), anyList());
    }

    @Test
    void testUpdate_Work_WhenEmptyListPayload(){
        // Arrange
        int itemId = 5;
        String requestCip = "dubw5596";
        String sellerCip = "dubw5596";

        ItemPayload payload = new ItemPayload();
        payload.tagList = List.of();
        payload.imageList = List.of();
        payload.paymentOptionList = List.of();
        payload.deliveryOptionList = List.of();
        Item item = new Item();
        item.sellerCip = sellerCip;

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock db return
        when(itemMapper.findById(itemId)).thenReturn(item);
        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act
        itemService.update(itemId, payload);

        // Assert
        Mockito.verify(itemMapper, Mockito.times(1)).update(anyString(), any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemTagMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemImageMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemImageMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemDeliveryOptionMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemDeliveryOptionMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemPaymentOptionMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemPaymentOptionMapper, Mockito.times(0)).insert(anyInt(), anyList());
    }

    @Test
    void testUpdate_Work_WhenNullListPayload(){
        // Arrange
        int itemId = 5;
        String requestCip = "dubw5596";
        String sellerCip = "dubw5596";

        ItemPayload payload = new ItemPayload();
        payload.tagList = null;
        payload.imageList = null;
        payload.deliveryOptionList = null;
        payload.paymentOptionList = null;
        Item item = new Item();
        item.sellerCip = sellerCip;

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock db return
        when(itemMapper.findById(itemId)).thenReturn(item);
        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act
        itemService.update(itemId, payload);

        // Assert
        Mockito.verify(itemMapper, Mockito.times(1)).update(anyString(), any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemTagMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemImageMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemImageMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemDeliveryOptionMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemDeliveryOptionMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemPaymentOptionMapper, Mockito.times(1)).deleteByItemId(anyInt());
        Mockito.verify(itemPaymentOptionMapper, Mockito.times(0)).insert(anyInt(), anyList());
    }

    @Test
    void testUpdate_ThrowNotFound_WhenItemInvalid(){
        // Arrange
        int itemId = 5;
        String requestCip = "dubw5596";

        ItemPayload payload = new ItemPayload();

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock db return
        when(itemMapper.findById(itemId)).thenReturn(null);
        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act and assert
        assertThrows(NotFoundException.class, () -> {
            itemService.update(itemId, payload);
        });

        Mockito.verify(itemMapper, Mockito.times(0)).update(anyString(), any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(0)).deleteByItemId(anyInt());
        Mockito.verify(itemTagMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemImageMapper, Mockito.times(0)).deleteByItemId(anyInt());
        Mockito.verify(itemImageMapper, Mockito.times(0)).insert(anyInt(), anyList());
    }

    @Test
    void testInsert_Works_WhenFullPayload(){
        // Arrange
        String requestCip = "dubw5596";

        ItemPayload payload = new ItemPayload();
        payload.tagList = List.of(1, 2, 3);
        payload.imageList = List.of(new ItemImageElement(), new ItemImageElement());

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act
        itemService.insert(payload);

        // Assert
        Mockito.verify(itemMapper, Mockito.times(1)).insert(any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(1)).insert(anyInt(), anyList());
        Mockito.verify(itemImageMapper, Mockito.times(1)).insert(anyInt(), anyList());
    }

    @Test
    void testInsert_Works_WhenNullListPayload(){
        // Arrange
        String requestCip = "dubw5596";

        ItemPayload payload = new ItemPayload();
        payload.imageList = null;
        payload.tagList = null;

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act
        itemService.insert(payload);

        // Assert
        Mockito.verify(itemMapper, Mockito.times(1)).insert(any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemImageMapper, Mockito.times(0)).insert(anyInt(), anyList());
    }

    @Test
    void testInsert_Works_WhenEmptyListPayload(){
        // Arrange
        String requestCip = "dubw5596";

        ItemPayload payload = new ItemPayload();
        payload.imageList = List.of();
        payload.tagList = List.of();

        // Mock request cip
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);

        // Mock converter
        when(itemConverter.toBusiness(payload)).thenReturn(new Item());

        // Act
        itemService.insert(payload);

        // Assert
        Mockito.verify(itemMapper, Mockito.times(1)).insert(any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(0)).insert(anyInt(), anyList());
        Mockito.verify(itemImageMapper, Mockito.times(0)).insert(anyInt(), anyList());
    }

    @Test
    void testAddFavoriteValidItemReturns201() {
        // arrange
        String requestCip = "pele3157";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);
        when(itemMapper.getItemCountById(1)).thenReturn(1);
        when(userMapper.getUserCountByCip(requestCip)).thenReturn(1);
        // act
        Response result = itemService.addFavorite(1);

        // assert
        assert(result.getStatus() == 201);
    }

    @Test
    void testAddFavoriteNonExistingItemReturns404() {
        // arrange
        when(itemMapper.getItemCountById(1)).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> itemService.removeFavorite(1));
    }

    @Test
    void testAddFavoriteUnauthorizedUserReturns401() {
        // arrange
        String requestCip = "pele3157";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);
        when(itemMapper.getItemCountById(1)).thenReturn(1);
        when(userMapper.getUserCountByCip(requestCip)).thenReturn(0);
        // act and assert
        assertThrows(UnauthorizedException.class, () -> itemService.removeFavorite(1));
    }

    @Test
    void testRemoveFavoriteValidItemReturns204() {
        // arrange
        String requestCip = "pele3157";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);
        when(itemMapper.getItemCountById(1)).thenReturn(1);
        when(userMapper.getUserCountByCip(requestCip)).thenReturn(1);
        // act
        Response result = itemService.removeFavorite(1);

        // assert
        assert(result.getStatus() == 204);
    }

    @Test
    void testRemoveFavoriteNonExistingItemReturns404() {
        // arrange
        when(itemMapper.getItemCountById(1)).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> itemService.removeFavorite(1));
    }

    @Test
    void testRemoveFavoriteUnauthorizedUserReturns401() {
        // arrange
        String requestCip = "pele3157";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);
        when(itemMapper.getItemCountById(1)).thenReturn(1);
        when(userMapper.getUserCountByCip(requestCip)).thenReturn(0);
        // act and assert
        assertThrows(UnauthorizedException.class, () -> itemService.removeFavorite(1));
    }
}

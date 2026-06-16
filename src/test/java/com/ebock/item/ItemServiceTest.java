package com.ebock.item;

import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.UserMapper;
import com.ebock.service.ItemService;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    ItemMapper itemMapper;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    ItemService itemService;

    @Test
    void testListReturnsListOfItems() {
        // arrange
        List<ItemResponse> expected = new ArrayList<>();
        when(itemMapper.getPaginatedItem(1, 25)).thenReturn(expected);

        // act
        List<ItemResponse> result = itemService.list(1);

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testItemDetailsReturnsItemInformation() {
        // arrange
        ItemDetailsResponse expected = new ItemDetailsResponse();
        when(itemMapper.findItemById(1)).thenReturn(1);
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
        when(userMapper.findUserByCip("larj4236")).thenReturn(1);
        when(itemMapper.getAllItemsSeller("larj4236")).thenReturn(expected);

        // act
        List<ItemResponse> result = itemService.cipStorefront("larj4236");

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testCipStorefrontUnknownCipThrowsNotFound() {
        // arrange
        when(userMapper.findUserByCip("abcd1234")).thenReturn(0);

        // act and assert
        assertThrows(NotFoundException.class, () -> itemService.cipStorefront("abcd1234"));
    }
}

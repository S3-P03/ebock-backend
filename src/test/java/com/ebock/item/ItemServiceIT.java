package com.ebock.item;

import com.ebock.business.Item;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.request.item.ItemImageElement;
import com.ebock.dto.request.item.ItemPayload;
import com.ebock.dto.response.item.ItemInsertResponse;
import com.ebock.mapper.ItemImageMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.ItemTagMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
public class ItemServiceIT {

    @InjectMock
    ItemMapper itemMapper;
    @InjectMock
    ItemImageMapper itemImageMapper;
    @InjectMock
    ItemTagMapper itemTagMapper;
    @InjectMock
    ItemConverter itemConverter;

    private ItemPayload validPayload;
    private Item mockedItem;
    private ItemInsertResponse mockedResponse;

    @BeforeEach
    public void setup() {
        // Arrange
        validPayload = new ItemPayload();
        validPayload.tagList = List.of(1,2);
        validPayload.imageList = List.of(new ItemImageElement(), new ItemImageElement());
        validPayload.categoryId = 1;
        validPayload.quantity = 1;
        validPayload.wearId = 1;
        validPayload.name = "asdf";
        validPayload.price = BigDecimal.valueOf(10);

        mockedItem = new Item();
        mockedItem.itemId = 99;
        mockedItem.sellerCip = "testuser";

        mockedResponse = new ItemInsertResponse();

        Mockito.when(itemConverter.toBusiness(any(ItemPayload.class))).thenReturn(mockedItem);
        Mockito.when(itemConverter.toInsertResponse(any(Item.class))).thenReturn(mockedResponse);
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    public void testInsert_Success() {
        // Act and assert
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/item/insert")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        Mockito.verify(itemMapper).insert(any(Item.class));
        Mockito.verify(itemTagMapper).insert(eq(99), eq(validPayload.tagList));
        Mockito.verify(itemImageMapper).insert(eq(99), Mockito.argThat(list ->
                        list != null && list.size() == validPayload.imageList.size()
        ));
    }

    @Test
    public void testInsert_Unauthorized_ShouldReturn401() {
        // Act and assert
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/item/insert")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    public void testUpdate_Success() {
        // Arrange
        Item existingItem = new Item();
        existingItem.sellerCip = "testuser";
        existingItem.itemId = 99;

        Mockito.when(itemMapper.findById(99)).thenReturn(existingItem);

        // Act and assert
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .put("/item/update/99")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        // Verification: Ensure old data is wiped and new data is inserted
        Mockito.verify(itemMapper, Mockito.times(1)).update(eq("testuser"), any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(1)).deleteByItemId(99);
        Mockito.verify(itemTagMapper, Mockito.times(1)).insert(eq(99), eq(validPayload.tagList));
        Mockito.verify(itemImageMapper, Mockito.times(1)).deleteByItemId(99);
        Mockito.verify(itemImageMapper).insert(eq(99), Mockito.argThat(list ->
                list != null && list.size() == validPayload.imageList.size()
        ));
    }

    @Test
    @TestSecurity(user = "hacker", roles = {"user"})
    public void testUpdate_Forbidden_NotOwner() {
        // Arrange
        Item existingItem = new Item();
        existingItem.sellerCip = "testuser";
        existingItem.itemId = 99;

        Mockito.when(itemMapper.findById(99)).thenReturn(existingItem);

        // Act and assert
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .put("/item/update/99")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Mockito.verify(itemMapper, Mockito.never()).update(any(), any());
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    public void testUpdate_NotFound() {
        // Arrange
        Mockito.when(itemMapper.findById(99)).thenReturn(null);

        // Act and assert
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .put("/item/update/99")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Mockito.verify(itemMapper, Mockito.never()).update(any(), any());
    }
}
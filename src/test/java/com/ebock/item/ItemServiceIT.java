package com.ebock.item;

import com.ebock.business.Item;
import com.ebock.converter.ItemConverter;
import com.ebock.dto.request.item.ItemCreatePayload;
import com.ebock.dto.request.item.ItemImageElement;
import com.ebock.dto.request.item.ItemUpdatePayload;
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

    private ItemCreatePayload validCreatePayload;
    private ItemUpdatePayload validUpdatePayload;
    private Item mockedItem;
    private ItemInsertResponse mockedResponse;

    @BeforeEach
    public void setup() {
        // Arrange
        validCreatePayload = new ItemCreatePayload();
        validCreatePayload.tagList = List.of(1,2);
        validCreatePayload.imageList = List.of(new ItemImageElement(), new ItemImageElement());
        validCreatePayload.categoryId = 1;
        validCreatePayload.quantity = 1;
        validCreatePayload.wearId = 1;
        validCreatePayload.name = "asdf";
        validCreatePayload.price = BigDecimal.valueOf(10);

        validUpdatePayload = new ItemUpdatePayload();
        validUpdatePayload.tagList = List.of(1,2);
        validUpdatePayload.imageList = List.of(new ItemImageElement(), new ItemImageElement());
        validUpdatePayload.categoryId = 1;
        validUpdatePayload.quantity = 1;
        validUpdatePayload.wearId = 1;
        validUpdatePayload.name = "asdf";
        validUpdatePayload.price = BigDecimal.valueOf(10);

        mockedItem = new Item();
        mockedItem.itemId = 99;
        mockedItem.sellerCip = "testuser";

        mockedResponse = new ItemInsertResponse();

        Mockito.when(itemConverter.toBusiness(any(ItemCreatePayload.class))).thenReturn(mockedItem);
        Mockito.when(itemConverter.toInsertResponse(any(Item.class))).thenReturn(mockedResponse);
        Mockito.when(itemConverter.toBusiness(any(ItemUpdatePayload.class))).thenReturn(mockedItem);
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    public void testInsert_Success() {
        // Act and assert
        given()
                .contentType(ContentType.JSON)
                .body(validCreatePayload)
                .when()
                .post("/item")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        Mockito.verify(itemMapper).insert(any(Item.class));
        Mockito.verify(itemTagMapper).insert(eq(99), eq(validCreatePayload.tagList));
        Mockito.verify(itemImageMapper).insert(eq(99), Mockito.argThat(list ->
                        list != null && list.size() == validCreatePayload.imageList.size()
        ));
    }

    @Test
    public void testInsert_Unauthorized_ShouldReturn401() {
        // Act and assert
        given()
                .contentType(ContentType.JSON)
                .body(validCreatePayload)
                .when()
                .post("/item")
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
                .body(validUpdatePayload)
                .when()
                .put("/item/99")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        // Verification: Ensure old data is wiped and new data is inserted
        Mockito.verify(itemMapper, Mockito.times(1)).update(eq("testuser"), any(Item.class));
        Mockito.verify(itemTagMapper, Mockito.times(1)).deleteByItemId(99);
        Mockito.verify(itemTagMapper, Mockito.times(1)).insert(eq(99), eq(validCreatePayload.tagList));
        Mockito.verify(itemImageMapper, Mockito.times(1)).deleteByItemId(99);
        Mockito.verify(itemImageMapper).insert(eq(99), Mockito.argThat(list ->
                list != null && list.size() == validCreatePayload.imageList.size()
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
                .body(validUpdatePayload)
                .when()
                .put("/item/99")
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
                .body(validUpdatePayload)
                .when()
                .put("/item/update/99")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Mockito.verify(itemMapper, Mockito.never()).update(any(), any());
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testDelete_ReturnsNoContent() {
        // Arrange
        Mockito.when(itemMapper.archiveRoomsById(99)).thenReturn(1);

        // Act & Assert
        given()
                .when()
                .delete("/item/99")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        Mockito.verify(itemMapper, Mockito.times(1)).delete(99);
        Mockito.verify(itemMapper, Mockito.times(1)).archiveRoomsById(99);
        Mockito.verify(itemMapper, Mockito.never()).getItemCountById(Mockito.anyInt());
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testDelete_ReturnsNotFound() {
        // Arrange
        Mockito.when(itemMapper.archiveRoomsById(99)).thenReturn(0);
        Mockito.when(itemMapper.getItemCountById(99)).thenReturn(0);

        // Act & Assert
        given()
                .when()
                .delete("/item/99")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Mockito.verify(itemMapper, Mockito.times(1)).delete(99);
        Mockito.verify(itemMapper, Mockito.times(1)).archiveRoomsById(99);
        Mockito.verify(itemMapper, Mockito.times(1)).getItemCountById(99);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testDelete_ReturnsNoContent_ItemExistsNoRoomsOpen() {
        // Arrange
        Mockito.when(itemMapper.archiveRoomsById(99)).thenReturn(0);
        Mockito.when(itemMapper.getItemCountById(99)).thenReturn(1);

        // Act & Assert
        given()
                .when()
                .delete("/item/99")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        Mockito.verify(itemMapper).delete(99);
        Mockito.verify(itemMapper).archiveRoomsById(99);
        Mockito.verify(itemMapper).getItemCountById(99);
    }
}
package com.ebock.item;

import com.ebock.dto.request.item.FilterItemPayload;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ItemFiltersIT {

    private FilterItemPayload validPayload;

    @BeforeEach
    void setUp(){
        validPayload = new FilterItemPayload();
        validPayload.favorite = false;
        validPayload.listCategoryId = List.of();
        validPayload.listTagId = List.of();
        validPayload.listWearId = List.of();
        validPayload.listDeliveryId = List.of();
        validPayload.listPaymentId = List.of();
    }

    @Test
    void list_Returns400_NegativePageNumber(){
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "-1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .statusCode(400);
    }

    @Test
    void list_ReturnsAllItems_NoFilters(){
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(1))
                .body("[1].itemId", is(2))
                .body("[2].itemId", is(3))
                .body("[3].itemId", is(5));

    }

    @Test
    void list_ReturnsItems_PriceFilters(){
        validPayload.minPrice = BigDecimal.valueOf(0);
        validPayload.maxPrice = BigDecimal.valueOf(1000);

        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(3))
                .body("[1].itemId", is(5));
    }


    @Test
    void list_ReturnsAllItems_FavoriteUserNotConnected(){
        validPayload.favorite = true;
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(1))
                .body("[1].itemId", is(2))
                .body("[2].itemId", is(3))
                .body("[3].itemId", is(5));
    }

    @Test
    @TestSecurity(user = "pele3157", roles = {"user"})
    void list_ReturnsItems_FavoriteUserConnected(){
        validPayload.favorite = true;
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(1));
    }

    @Test
    void list_ReturnsItems_TagFilter(){
        validPayload.listTagId = List.of(2);
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(3))
                .body("[1].itemId", is(5));
    }

    @Test
    void list_ReturnsItems_MultipleTagFilters(){
        validPayload.listTagId = List.of(1, 2);
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(5));
    }

    @Test
    @TestSecurity(user = "pele3157", roles = {"user"})
    void list_ReturnsItems_WithAllFilters(){
        validPayload.minPrice = BigDecimal.valueOf(2000);
        validPayload.maxPrice = BigDecimal.valueOf(3000);
        validPayload.favorite = true;
        validPayload.listCategoryId = List.of(2);
        validPayload.listTagId = List.of(3);
        validPayload.listWearId = List.of(1);
        validPayload.listDeliveryId = List.of(1,2);
        validPayload.listPaymentId = List.of(2);

        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("pageNumber", "1")
                .when()
                .post("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(1));
    }
}

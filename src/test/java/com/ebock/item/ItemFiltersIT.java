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

    @Test
    void list_Returns400_NegativePageNumber(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("pageNumber", "-1")
                .when()
                .get("/item/list/{pageNumber}")
                .then()
                .statusCode(400);
    }

    @Test
    void list_ReturnsAllItems_NoFilters(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("pageNumber", "1")
                .when()
                .get("/item/list/{pageNumber}")
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
        given()
                .contentType(ContentType.JSON)
                .queryParams("minP", 0,
                        "maxP", 1000)
                .pathParam("pageNumber", "1")
                .when()
                .get("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(3))
                .body("[1].itemId", is(5));
    }


    @Test
    void list_ReturnsAllItems_FavoriteUserNotConnected(){
        given()
                .contentType(ContentType.JSON)
                .queryParam("fav", true)
                .pathParam("pageNumber", "1")
                .when()
                .get("/item/list/{pageNumber}")
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
        given()
                .contentType(ContentType.JSON)
                .queryParam("fav", true)
                .pathParam("pageNumber", "1")
                .when()
                .get("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(1));
    }

    @Test
    void list_ReturnsItems_TagFilter(){
        given()
                .contentType(ContentType.JSON)
                .queryParam("tags", "2")
                .pathParam("pageNumber", "1")
                .when()
                .get("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(3))
                .body("[1].itemId", is(5));
    }

    @Test
    void list_ReturnsItems_MultipleTagFilters(){
        given()
                .contentType(ContentType.JSON)
                .queryParam("tags", "1,2")
                .pathParam("pageNumber", "1")
                .when()
                .get("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(5));
    }

    @Test
    @TestSecurity(user = "pele3157", roles = {"user"})
    void list_ReturnsItems_WithAllFilters(){
        given()
                .contentType(ContentType.JSON)
                .queryParams("minP", 2000,
                        "maxP", 3000,
                        "fav", true,
                        "categories", "2",
                        "tags", "3",
                        "wears", "1",
                        "deliveries", "1,2",
                        "payments", "2")
                .pathParam("pageNumber", "1")
                .when()
                .get("/item/list/{pageNumber}")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].itemId", is(1));
    }
}

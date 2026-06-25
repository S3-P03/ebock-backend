package com.ebock.review;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;

@QuarkusTest
public class ReviewIT {

    @Test
    void reviewAvg_Returns404_InexistentCip(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("cip", "abcd1234")
                .when()
                .get("/review/{cip}/average")
                .then()
                .statusCode(404);
    }

    @Test
    void reviewAvg_ReturnsAvg_UserWithReviews(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("cip", "pele3157")
                .when()
                .get("/review/{cip}/average")
                .then()
                .statusCode(200)
                .body("avgRating", is(3.33f))
                .body("nbrReviews", is(3));
    }

    @Test
    void reviewAvg_ReturnsAvg_UserNoReviews(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("cip", "bela3439")
                .when()
                .get("/review/{cip}/average")
                .then()
                .statusCode(200)
                .body("avgRating", is(0f))
                .body("nbrReviews", is(0));
    }

    @Test
    void reviewDetails_Returns404_InexistentCip(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("cip", "abcd1234")
                .when()
                .get("/review/{cip}/details")
                .then()
                .statusCode(404);
    }

    @Test
    void reviewDetails_ReturnsReviews_UserWithReviews(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("cip", "pele3157")
                .when()
                .get("/review/{cip}/details")
                .then()
                .statusCode(200)
                .body("[0].firstName", is("Jean-Félix"))
                .body("[1].firstName", is("Léanne"))
                .body("[2].firstName", is("William"))
                .body("[2].lastName", is("Dubuc"))
                .body("[2].content", is("67777777777777"))
                .body("[2].rating", is(4));
    }

    @Test
    void reviewDetails_ReturnsReviews_UserNoReviews(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("cip", "bela3439")
                .when()
                .get("/review/{cip}/details")
                .then()
                .statusCode(200)
                .body("$", empty());
    }
}

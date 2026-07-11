package com.ebock.comment;

import com.ebock.dto.request.comment.CommentPayload;
import com.ebock.mapper.CommentMapper;
import com.ebock.mapper.ItemMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CommentIT {

    private CommentPayload validCommentPayload;

    @BeforeEach
    public void setup() {
        validCommentPayload = new CommentPayload();
        validCommentPayload.content = "Test commentaire";
        validCommentPayload.idParent = null;
    }

    @Test
    void commentDetails_Returns404_InexistentItem() {
        given()
                .pathParam("id", 10)
                .when()
                .get("/item/{id}/comment")
                .then()
                .statusCode(404);
    }

    @Test
    void commentDetails_Returns200_ExistentItem() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/item/{id}/comment")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    void commentInsert_Returns400_InexistentItem() {
        given()
                .contentType(ContentType.JSON)
                .body(validCommentPayload)
                .pathParam("id", 111)
                .when()
                .post("/item/{id}/comment")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "pele3157", roles = {"user"})
    void commentInsert_CreatesComment_ExistentItem() {
        given()
                .contentType(ContentType.JSON)
                .body(validCommentPayload)
                .pathParam("id", 1)
                .when()
                .post("/item/{id}/comment")
                .then()
                .statusCode(201);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void commentDelete_DeletesComment_() {
        given()
                .pathParam("id", 1)
                .when()
                .delete("/comment/{id}")
                .then()
                .statusCode(204);

    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void commentDelete_Returns404_InexistentComment() {
        given()
                .pathParam("id", 111)
                .when()
                .delete("/comment/{id}")
                .then()
                .statusCode(404);

    }
}
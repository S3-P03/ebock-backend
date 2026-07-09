package com.ebock.comment;

import com.ebock.dto.request.comment.CommentPayload;
import com.ebock.mapper.CommentMapper;
import com.ebock.mapper.ItemMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
public class CommentIT {

    @InjectMock
    CommentMapper commentMapper;

    @InjectMock
    ItemMapper itemMapper;

    private CommentPayload validPayload;

    @BeforeEach
    public void setup() {
        validPayload = new CommentPayload();
        validPayload.content = "Test commentaire";
    }

    @Test
    void commentDetails_Returns404_InexistentItem() {

        Mockito.when(itemMapper.getItemCountById(10)).thenReturn(0);

        given()
                .pathParam("id", 10)
                .when()
                .get("/comment/{id}/details")
                .then()
                .statusCode(404);
    }

    @Test
    void commentDetails_Returns200_ExistentItem() {

        Mockito.when(itemMapper.getItemCountById(1)).thenReturn(1);
        Mockito.when(commentMapper.getDetailledComments(1))
                .thenReturn(new ArrayList<>());

        given()
                .pathParam("id", 1)
                .when()
                .get("/comment/{id}/details")
                .then()
                .statusCode(200);

        Mockito.verify(commentMapper, Mockito.times(1))
                .getDetailledComments(1);
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    void commentInsert_Returns404_InexistentItem() {

        Mockito.when(itemMapper.getItemCountById(10)).thenReturn(0);

        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("id", 10)
                .when()
                .post("/comment/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    void commentInsert_CreatesComment_ExistentItem() {

        Mockito.when(itemMapper.getItemCountById(1)).thenReturn(1);

        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("id", 1)
                .when()
                .post("/comment/{id}")
                .then()
                .statusCode(201);

        Mockito.verify(commentMapper, Mockito.times(1))
                .insert(eq(1), eq("testuser"), any(CommentPayload.class));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void commentDelete_Returns404_InexistentItem() {

        Mockito.when(itemMapper.getItemCountById(10)).thenReturn(0);

        given()
                .pathParam("id", 10)
                .when()
                .delete("/comment/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void commentDelete_DeletesComment_ExistentItem() {

        Mockito.when(itemMapper.getItemCountById(1)).thenReturn(1);

        given()
                .pathParam("id", 1)
                .when()
                .delete("/comment/{id}")
                .then()
                .statusCode(204);

        Mockito.verify(commentMapper, Mockito.times(1))
                .delete(1);
    }

}
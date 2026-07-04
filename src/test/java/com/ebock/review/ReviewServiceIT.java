package com.ebock.review;

import com.ebock.dto.request.review.ReviewPayload;
import com.ebock.mapper.MessageMapper;
import com.ebock.mapper.ReviewMapper;
import com.ebock.mapper.UserMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
public class ReviewServiceIT {

    @InjectMock
    ReviewMapper reviewMapper;
    @InjectMock
    UserMapper userMapper;
    @InjectMock
    MessageMapper messageMapper;

    private ReviewPayload validPayload;

    @BeforeEach
    public void setup(){
        validPayload = new ReviewPayload();
        validPayload.content = "test";
        validPayload.rating = 5;
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    void reviewInsert_Returns404_InexistentCip(){
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("cip", "abcd1234")
                .when()
                .post("/review/{cip}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "bela3439", roles = {"user"})
    void reviewInsert_Returns403_NoMessagesWithSeller(){
        Mockito.when(userMapper.getUserCountByCip("pele3157")).thenReturn(1);

        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("cip", "pele3157")
                .when()
                .post("/review/{cip}")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "larj4236", roles = {"user"})
    void reviewInsert_Updates_ReviewExists(){
        Mockito.when(userMapper.getUserCountByCip("pele3157")).thenReturn(1);
        Mockito.when(messageMapper.getSellerReplyCountByBuyer("larj4236", "pele3157")).thenReturn(1);
        Mockito.when(reviewMapper.getCountByUsers("larj4236", "pele3157")).thenReturn(1);

        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("cip", "pele3157")
                .when()
                .post("/review/{cip}")
                .then()
                .statusCode(200);

        Mockito.verify(reviewMapper, Mockito.times(1)).update(eq("larj4236"), eq("pele3157"), any(ReviewPayload.class));
    }


    @Test
    @TestSecurity(user = "dubw5596", roles = {"user"})
    void reviewInsert_Inserts_ReviewDoesntExist(){
        Mockito.when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        Mockito.when(messageMapper.getSellerReplyCountByBuyer("dubw5596", "larj4236")).thenReturn(1);
        Mockito.when(reviewMapper.getCountByUsers("dubw5596", "larj4236")).thenReturn(0);

        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .pathParam("cip", "larj4236")
                .when()
                .post("/review/{cip}")
                .then()
                .statusCode(200);

        Mockito.verify(reviewMapper, Mockito.times(1)).insert(eq("dubw5596"), eq("larj4236"), any(ReviewPayload.class));
    }
}


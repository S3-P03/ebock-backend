package com.ebock.message;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
public class MessageIT {

    @InjectMock
    SecurityContext securityContext;

    @BeforeEach
    void setup() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("larj4236");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
    }

    @Test
    @TestSecurity(user = "larj4236")
    void queryRoomInfoExistingRoom() {
        given()
                .pathParam("id", 6)
                .when()
                .get("/message/room/{id}")
                .then()
                .statusCode(200)
                .body("roomId", is(6))
                .body("itemId", is(5))
                .body("itemName", is("Prise de laptop"))
                .body("sellerCip", is("pele3157"))
                .body("sellerFirstName", is("Éliane"))
                .body("sellerLastName", is("Pelletier"))
                .body("buyerCip", is("larj4236"))
                .body("buyerFirstName", is("Jean-Félix"))
                .body("buyerLastName", is("Larouche"));

    }

    @Test
    @TestSecurity(user = "larj4236")
    void queryRoomInfoNonExistingRoomReturns404() {
        given()
                .log().all()
                .pathParam("id", 456)
                .when()
                .get("/message/room/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "wrong1234")
    void queryRoomInfoNonExistingUserReturns404() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("wrong1234");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        given()
                .log().all()
                .pathParam("id", 6)
                .when()
                .get("/message/room/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "boum7113")
    void queryRoomInfoUnauthorizedUserReturns401() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("boum7113");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        given()
                .log().all()
                .pathParam("id", 6)
                .when()
                .get("/message/room/{id}")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "larj4236")
    void queryMessagesReturnsRoomMessages() {
        given()
                .pathParam("id", 6)
                .when()
                .get("/message/room/{id}/messages")
                .then()
                .statusCode(200)
                .body("[0].roomId", is(6))
                .body("[0].content", is("Salut !"))
                .body("[0].senderCip", is("larj4236"))
                .body("[0].senderFirstName", is("Jean-Félix"))
                .body("[0].senderLastName", is("Larouche"))
                .body("[0].sentAt", is("2026-06-17 13:59:05.849555"));
    }

    @Test
    @TestSecurity(user = "larj4236")
    void queryMessagesNonExistingRoomReturns404() {
        given()
                .log().all()
                .pathParam("id", 456)
                .when()
                .get("/message/room/{id}/messages")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "wrong1234")
    void queryMessagesNonExistingUserReturns404() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("wrong1234");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        given()
                .log().all()
                .pathParam("id", 6)
                .when()
                .get("/message/room/{id}/messages")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "boum7113")
    void queryMessagesUnauthorizedUserReturns401() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("boum7113");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        given()
                .log().all()
                .pathParam("id", 6)
                .when()
                .get("/message/room/{id}/messages")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "pele3157")
    void queryUserRoomsReturnsRooms() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("pele3157");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        given()
                .when()
                .get("/message/room")
                .then()
                .statusCode(200)
                .body("[0].roomId", is(6))
                .body("[0].itemId", is(5))
                .body("[0].itemName", is("Prise de laptop"))
                .body("[0].sellerCip", is("pele3157"))
                .body("[0].sellerFirstName", is("Éliane"))
                .body("[0].sellerLastName", is("Pelletier"))
                .body("[0].buyerCip", is("larj4236"))
                .body("[0].buyerFirstName", is("Jean-Félix"))
                .body("[0].buyerLastName", is("Larouche"));
    }

    @Test
    @TestSecurity(user = "bela3439")
    void queryUserRoomsEmptyReturnsEmptyList() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("bela3439");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        given()
                .when()
                .get("/message/room")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    @TestSecurity(user = "bad1234")
    void queryUserRoomsNonExistingUserReturns404() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("bad1234");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
        given()
                .when()
                .get("/message/room")
                .then()
                .statusCode(404);
    }
}

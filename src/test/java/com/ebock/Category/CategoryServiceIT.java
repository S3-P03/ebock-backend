package com.ebock.Category;

import com.ebock.business.Category;
import com.ebock.converter.CategoryConverter;
import com.ebock.dto.request.category.CategoryPayload;
import com.ebock.dto.response.category.CategoryResponse;
import com.ebock.mapper.CategoryMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
public class CategoryServiceIT {

    @InjectMock
    CategoryMapper categoryMapper;

    @InjectMock
    CategoryConverter categoryConverter;

    private CategoryPayload validPayload;
    private Category mockedCategory;
    private CategoryResponse mockedResponse;

    @BeforeEach
    public void setup() {
        validPayload = new CategoryPayload();
        validPayload.name = "asdf";

        mockedCategory = new Category();
        mockedCategory.categoryId = 1;

        mockedResponse = new CategoryResponse();

        Mockito.when(categoryConverter.toBusiness(any(CategoryPayload.class))).thenReturn(mockedCategory);
        Mockito.when(categoryConverter.toResponse(any(Category.class))).thenReturn(mockedResponse);
        Mockito.when(categoryConverter.toResponse(any(List.class))).thenReturn(List.of(mockedResponse));
    }

    @Test
    public void testList_PermitAll_Success() {
        // Arrange
        Mockito.when(categoryMapper.getAllCategories()).thenReturn(List.of(mockedCategory));

        given()
                .when()
                .get("/category/list")
                .then()
                .statusCode(200);

        Mockito.verify(categoryMapper, Mockito.times(1)).getAllCategories();
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testInsert_Authenticated_Success() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/category/insert")
                .then()
                .statusCode(200);

        Mockito.verify(categoryMapper, Mockito.times(1)).insert(mockedCategory);
    }

    @Test
    public void testInsert_Unauthenticated_ShouldReturn401() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/category/insert")
                .then()
                .statusCode(401);

        Mockito.verify(categoryMapper, Mockito.never()).insert(any());
    }

    @Test
    public void testUpdate_Unauthenticated_ShouldReturn401() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .put("/category/update/1")
                .then()
                .statusCode(401);

        Mockito.verify(categoryMapper, Mockito.never()).insert(any());
    }
}
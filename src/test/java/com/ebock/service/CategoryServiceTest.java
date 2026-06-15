package com.ebock.service;

import com.ebock.business.Category;
import com.ebock.converter.CategoryConverter;
import com.ebock.dto.response.category.CategoryPayload;
import com.ebock.dto.response.category.CategoryResponse;
import com.ebock.mapper.CategoryMapper;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryMapper categoryMapper;

    @Mock
    CategoryConverter categoryConverter;

    @InjectMocks
    CategoryService categoryService;

    @Test
    void testListReturnsCategoryList() {
        // arrange
        List<Category> categories = new ArrayList<>();
        List<CategoryResponse> expected = new ArrayList<>();
        when(categoryMapper.getAllCategories()).thenReturn(categories);
        when(categoryConverter.toResponse(categories)).thenReturn(expected);

        // act
        List<CategoryResponse> result = categoryService.list();

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertCallsInsertAndReturnsCreatedObject() {
        // arrange
        CategoryPayload catPayload = new CategoryPayload();
        CategoryResponse expected = new CategoryResponse();
        Category category = new Category();
        when(categoryConverter.toBusiness(catPayload)).thenReturn(category);
        when(categoryConverter.toResponse(category)).thenReturn(expected);

        // act
        CategoryResponse result = categoryService.insert(catPayload);

        // assert
        verify(categoryMapper, times(1)).insert(category);
        assertEquals(expected, result);
    }

    @Test
    void testUpdateCallsUpdateAndReturnsUpdatedOject() {
        // arrange
        CategoryPayload catPayload = new CategoryPayload();
        CategoryResponse expected = new CategoryResponse();
        Category category = new Category();
        when(categoryConverter.toBusiness(catPayload)).thenReturn(category);
        when(categoryConverter.toResponse(category)).thenReturn(expected);

        // act
        CategoryResponse result = categoryService.update(0, catPayload);

        // assert
        verify(categoryMapper, times(1)).update(category);
        assertEquals(expected, result);
    }
}

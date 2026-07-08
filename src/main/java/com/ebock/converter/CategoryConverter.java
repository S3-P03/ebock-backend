package com.ebock.converter;

import com.ebock.business.Category;
import com.ebock.dto.request.category.CategoryPayload;
import com.ebock.dto.response.category.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface CategoryConverter {
    CategoryResponse toResponse(Category category);
    List<CategoryResponse> toResponse(List<Category> categories);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Category toBusiness(CategoryPayload payload);
}


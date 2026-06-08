package com.ebock.converter;

import com.ebock.business.Category;
import com.ebock.dto.response.category.CategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface CategoryConverter {
    List<CategoryResponse> toResponse(List<Category> categories);
}


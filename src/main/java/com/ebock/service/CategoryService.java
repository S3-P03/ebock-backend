package com.ebock.service;

import com.ebock.business.Category;
import com.ebock.converter.CategoryConverter;
import com.ebock.dto.response.category.CategoryPayload;
import com.ebock.dto.response.category.CategoryResponse;
import com.ebock.mapper.CategoryMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryService {
    @Inject
    CategoryMapper categoryMapper;
    @Inject
    CategoryConverter categoryConverter;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/list/")
    @PermitAll
    public List<CategoryResponse> list() {
        List<Category> categories = this.categoryMapper.getAllCategories();
        return categoryConverter.toResponse(categories);
    }

    @POST
    @Path("/insert/")
    @Authenticated
    public CategoryResponse insert(CategoryPayload payload) {
        Category category = categoryConverter.toBusiness(payload);
        this.categoryMapper.insert(category);
        return categoryConverter.toResponse(category);
    }

    @PUT
    @Path("/update/{id}")
    @Authenticated
    public CategoryResponse insert(@PathParam("id") int id, CategoryPayload payload) {
        Category category = categoryConverter.toBusiness(payload);
        category.categoryId = id;
        this.categoryMapper.update(category);
        return categoryConverter.toResponse(category);
    }
}

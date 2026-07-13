package com.ebock.service;

import com.ebock.business.Category;
import com.ebock.converter.CategoryConverter;
import com.ebock.dto.request.category.CategoryPayload;
import com.ebock.dto.response.category.CategoryResponse;
import com.ebock.mapper.CategoryMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryService {
    @Inject
    CategoryMapper categoryMapper;
    @Inject
    CategoryConverter categoryConverter;

    @GET
    @Path("")
    @PermitAll
    public List<CategoryResponse> list() {
        List<Category> categories = this.categoryMapper.getAllCategories();
        return categoryConverter.toResponse(categories);
    }

    @POST
    @Path("")
    @RolesAllowed("admin")
    public CategoryResponse insert(@Valid CategoryPayload payload) {
        Category category = categoryConverter.toBusiness(payload);
        this.categoryMapper.insert(category);
        return categoryConverter.toResponse(category);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public CategoryResponse update(@PathParam("id") int id, @Valid CategoryPayload payload) {
        Category category = categoryConverter.toBusiness(payload);
        category.categoryId = id;
        this.categoryMapper.update(category);
        return categoryConverter.toResponse(category);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") int id) {
        categoryMapper.delete(id);
        return Response.noContent().build();
    }
}

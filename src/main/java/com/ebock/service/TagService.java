package com.ebock.service;

import com.ebock.business.Tag;
import com.ebock.converter.TagConverter;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.dto.response.tag.TagResponse;
import com.ebock.mapper.ItemMapper;
import com.ebock.mapper.TagMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/tag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagService {
    @Inject
    TagMapper tagMapper;
    @Inject
    TagConverter tagConverter;
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/list/")
    @PermitAll
    public List<TagResponse> list() {
        List<Tag> tags = this.tagMapper.getAllTags();
        return tagConverter.toResponse(tags);
    }
}

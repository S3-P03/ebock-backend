package com.ebock.converter;

import com.ebock.business.Tag;
import com.ebock.dto.response.tag.TagResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface TagConverter {
    List<TagResponse> toResponse(List<Tag> tags);
}


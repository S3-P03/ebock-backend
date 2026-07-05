package com.ebock.converter;

import com.ebock.business.Tag;
import com.ebock.dto.request.tag.TagPayload;
import com.ebock.dto.response.tag.TagResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface TagConverter {
    TagResponse toResponse(Tag tags);
    List<TagResponse> toResponse(List<Tag> tags);

    @Mapping(target = "tagId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Tag toBusiness(TagPayload payload);
}


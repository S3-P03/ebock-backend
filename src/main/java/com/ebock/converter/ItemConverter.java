package com.ebock.converter;

import com.ebock.business.Item;
import com.ebock.dto.response.ItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ItemConverter {
    ItemResponse toResponse(Item item);
    List<ItemResponse> toResponse(List<Item> list);
}

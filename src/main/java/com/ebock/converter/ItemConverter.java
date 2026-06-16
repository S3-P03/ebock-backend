package com.ebock.converter;

import com.ebock.business.Item;
import com.ebock.dto.payload.item.ItemInsertPayload;
import com.ebock.dto.response.item.ItemInsertResponse;
import com.ebock.dto.response.item.ItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ItemConverter {
    ItemResponse toResponse(Item item);
    List<ItemResponse> toResponse(List<Item> list);
    Item toBusiness(ItemInsertPayload payload);
    ItemInsertResponse toInsertResponse(Item item);
}

package com.ebock.converter;

import com.ebock.business.Item;
import com.ebock.dto.request.item.ItemCreatePayload;
import com.ebock.dto.request.item.ItemUpdatePayload;
import com.ebock.dto.response.item.ItemInsertResponse;
import com.ebock.dto.response.item.ItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ItemConverter {
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "favorite", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "firstImage", ignore = true)
    ItemResponse toResponse(Item item);
    List<ItemResponse> toResponse(List<Item> list);

    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sold", ignore = true)
    @Mapping(target = "archived", ignore = true)
    @Mapping(target = "sellerCip", ignore = true)
    Item toBusiness(ItemCreatePayload payload);

    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sold", ignore = true)
    @Mapping(target = "archived", ignore = true)
    @Mapping(target = "sellerCip", ignore = true)
    Item toBusiness(ItemUpdatePayload payload);
    ItemInsertResponse toInsertResponse(Item item);
}

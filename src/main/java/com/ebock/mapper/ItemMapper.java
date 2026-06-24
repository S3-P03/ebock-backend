package com.ebock.mapper;

import com.ebock.business.Item;
import com.ebock.business.Wear;
import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.dto.request.item.FilterItemPayload;
import com.ebock.dto.response.item.ItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    //tous les items pas archived du vendeur
    List<ItemResponse> getAllItemsSeller(@Param("cip") String cip);
    ItemDetailsResponse getItemDetails(@Param("id") int id);
    int getItemCountById(@Param("id") int id);
    List<ItemResponse> getPaginatedItem(@Param("pageNumber") int pageNumber,
                                        @Param("pageSize") int pageSize,
                                        @Param("filters") FilterItemPayload filterItemPayload,
                                        @Param("cip") String cip);
    void favorite(@Param("id") int itemId, @Param("cip") String cip);
    void unfavorite(@Param("id") int itemId, @Param("cip") String cip);
    Item findById(@Param("id") int id);
    void insert(@Param("item") Item item);
    void update(@Param("sellerCip") String sellerCip, @Param("item") Item item);
}

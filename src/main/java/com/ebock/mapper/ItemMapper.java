package com.ebock.mapper;

import com.ebock.dto.response.item.ItemDetailsResponse;
import com.ebock.dto.response.item.ItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    //tous les items pas archived du vendeur
    List<ItemResponse> getAllItemsSeller(@Param("cip") String cip);
    List<ItemResponse> getPaginatedItem(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);
    ItemDetailsResponse getItemDetails(@Param("id") int id);
    int findItemById(@Param("id") int id);
}
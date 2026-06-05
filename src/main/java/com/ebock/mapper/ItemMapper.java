package com.ebock.mapper;

import com.ebock.business.Item;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.dto.response.user.ForeignUserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    //tous les items pas sold et pas archived
    List<ItemResponse> getAllItemsInfo();
    //tous les items pas archived du vendeur
    List<ItemResponse> getAllItemsSeller(@Param("cip") String cip);
}

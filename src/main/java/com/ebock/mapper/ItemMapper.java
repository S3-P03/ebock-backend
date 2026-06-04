package com.ebock.mapper;

import com.ebock.dto.response.item.ItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    //tous les items pas sold et pas archived
    List<ItemResponse> getPaginatedItem(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);
}

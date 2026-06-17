package com.ebock.mapper;

import com.ebock.dto.request.item.ItemImageElement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemImageMapper {
    void insert(@Param("itemId") int itemId, @Param("images") List<ItemImageElement> images);
}

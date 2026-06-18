package com.ebock.mapper;

import com.ebock.business.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemTagMapper {
    void deleteByItemId(@Param("itemId") int itemId);
    void insert(@Param("itemId") int itemId, @Param("tags") List<Integer> tags);
}

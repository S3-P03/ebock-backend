package com.ebock.mapper;

import com.ebock.business.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ItemMapper {
    //tous les items pas sold et pas archived
    List<Item> getAllItemsInfo();
}

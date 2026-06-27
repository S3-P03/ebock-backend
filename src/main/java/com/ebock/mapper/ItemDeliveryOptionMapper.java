package com.ebock.mapper;

import com.ebock.business.ItemDeliveryOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemDeliveryOptionMapper {
    void deleteByItemId(@Param("itemId") int itemId);
    void insert(@Param("itemId") int itemId, @Param("deliveryOptions") List<Integer> deliveryOptions);
}

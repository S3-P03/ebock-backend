package com.ebock.mapper;

import com.ebock.business.ItemPaymentOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemPaymentOptionMapper {
    void deleteByItemId(@Param("itemId") int itemId);
    void insert(@Param("itemId") int itemId, @Param("paymentOptions") List<Integer> paymentOptions);
}

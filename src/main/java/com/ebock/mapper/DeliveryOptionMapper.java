package com.ebock.mapper;

import com.ebock.business.DeliveryOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeliveryOptionMapper {
    List<DeliveryOption> getAllDeliveryOptions();
    void insert(@Param("deliveryOption") DeliveryOption deliveryOption);
    void update(@Param("deliveryOption") DeliveryOption deliveryOption);
}

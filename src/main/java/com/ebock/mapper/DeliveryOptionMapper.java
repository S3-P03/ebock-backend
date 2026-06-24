package com.ebock.mapper;

import com.ebock.business.DeliveryOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeliveryOptionMapper {
    List<DeliveryOption> getAllDeliveryOptions();
}

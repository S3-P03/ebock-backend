package com.ebock.mapper;

import com.ebock.business.PaymentOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentOptionMapper {
    List<PaymentOption> getAllPaymentOptions();
}

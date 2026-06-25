package com.ebock.mapper;

import com.ebock.business.PaymentOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentOptionMapper {
    List<PaymentOption> getAllPaymentOptions();
    void insert(@Param("paymentOption") PaymentOption paymentOption);
    void update(@Param("paymentOption") PaymentOption paymentOption);
}

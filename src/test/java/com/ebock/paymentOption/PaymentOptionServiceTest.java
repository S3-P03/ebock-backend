package com.ebock.paymentOptionOption;

import com.ebock.business.PaymentOption;
import com.ebock.converter.PaymentOptionConverter;
import com.ebock.dto.request.paymentOption.PaymentOptionPayload;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import com.ebock.mapper.PaymentOptionMapper;
import com.ebock.service.PaymentOptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentOptionServiceTest {
    @Mock
    PaymentOptionMapper paymentOptionMapper;
    @Mock
    PaymentOptionConverter paymentOptionConverter;
    @InjectMocks
    PaymentOptionService paymentOptionService;

    @Test
    void testListReturnsTagList() {
        // arrange
        List<PaymentOption> paymentOptions = new ArrayList<>();
        List<PaymentOptionResponse> expected = new ArrayList<>();
        when(paymentOptionMapper.getAllPaymentOptions()).thenReturn(paymentOptions);
        when(paymentOptionConverter.toResponse(paymentOptions)).thenReturn(expected);

        // act
        List<PaymentOptionResponse> result = paymentOptionService.list();

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertCallsInsertAndReturnsCreatedObject() {
        // arrange
        PaymentOptionPayload paymentOptionPayload = new PaymentOptionPayload();
        PaymentOptionResponse expected = new PaymentOptionResponse();
        PaymentOption paymentOption = new PaymentOption();
        when(paymentOptionConverter.toBusiness(paymentOptionPayload)).thenReturn(paymentOption);
        when(paymentOptionConverter.toResponse(paymentOption)).thenReturn(expected);

        // act
        PaymentOptionResponse result = paymentOptionService.insert(paymentOptionPayload);

        // assert
        verify(paymentOptionMapper, times(1)).insert(paymentOption);
        assertEquals(expected, result);
    }

    @Test
    void testUpdateCallsUpdateAndReturnsUpdatedOject() {
        // arrange
        PaymentOptionPayload paymentOptionPayload = new PaymentOptionPayload();
        PaymentOptionResponse expected = new PaymentOptionResponse();
        PaymentOption paymentOption = new PaymentOption();
        when(paymentOptionConverter.toBusiness(paymentOptionPayload)).thenReturn(paymentOption);
        when(paymentOptionConverter.toResponse(paymentOption)).thenReturn(expected);

        // act
        PaymentOptionResponse result = paymentOptionService.update(0, paymentOptionPayload);

        // assert
        verify(paymentOptionMapper, times(1)).update(paymentOption);
        assertEquals(expected, result);
    }
}

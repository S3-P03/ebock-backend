package com.ebock.PaymentOption;

import com.ebock.business.PaymentOption;
import com.ebock.converter.PaymentOptionConverter;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import com.ebock.mapper.PaymentOptionMapper;
import com.ebock.service.PaymentOptionService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testListReturnsPaymentOptionList() {
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
    void testDeleteCallsDeleteAndReturnsResult() {
        // arrange
        int paymentOptnId = 0;
        when(paymentOptionMapper.getCountById(paymentOptnId)).thenReturn(1);
        // act
        Response result = paymentOptionService.delete(paymentOptnId);

        // assert
        verify(paymentOptionMapper, times(1)).delete(paymentOptnId);
        assertEquals(204, result.getStatus());
    }

    @Test
    void testDelete_ThrowsNotFound_Inexistent() {
        // arrange
        int paymentOptnId = 0;
        when(paymentOptionMapper.getCountById(paymentOptnId)).thenReturn(0);
        // act and assert
        assertThrows(NotFoundException.class, () -> paymentOptionService.delete(paymentOptnId));
    }
}

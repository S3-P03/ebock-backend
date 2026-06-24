package com.ebock.deliveryOption;

import com.ebock.business.DeliveryOption;
import com.ebock.converter.DeliveryOptionConverter;
import com.ebock.dto.request.deliveryOption.DeliveryOptionPayload;
import com.ebock.dto.response.deliveryOption.DeliveryOptionResponse;
import com.ebock.mapper.DeliveryOptionMapper;
import com.ebock.service.DeliveryOptionService;
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
public class DeliveryOptionServiceTest {
    @Mock
    DeliveryOptionMapper deliveryOptionMapper;
    @Mock
    DeliveryOptionConverter deliveryOptionConverter;
    @InjectMocks
    DeliveryOptionService deliveryOptionService;

    @Test
    void testListReturnsTagList() {
        // arrange
        List<DeliveryOption> deliveryOptions = new ArrayList<>();
        List<DeliveryOptionResponse> expected = new ArrayList<>();
        when(deliveryOptionMapper.getAllDeliveryOptions()).thenReturn(deliveryOptions);
        when(deliveryOptionConverter.toResponse(deliveryOptions)).thenReturn(expected);

        // act
        List<DeliveryOptionResponse> result = deliveryOptionService.list();

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertCallsInsertAndReturnsCreatedObject() {
        // arrange
        DeliveryOptionPayload deliveryOptionPayload = new DeliveryOptionPayload();
        DeliveryOptionResponse expected = new DeliveryOptionResponse();
        DeliveryOption deliveryOption = new DeliveryOption();
        when(deliveryOptionConverter.toBusiness(deliveryOptionPayload)).thenReturn(deliveryOption);
        when(deliveryOptionConverter.toResponse(deliveryOption)).thenReturn(expected);

        // act
        DeliveryOptionResponse result = deliveryOptionService.insert(deliveryOptionPayload);

        // assert
        verify(deliveryOptionMapper, times(1)).insert(deliveryOption);
        assertEquals(expected, result);
    }

    @Test
    void testUpdateCallsUpdateAndReturnsUpdatedOject() {
        // arrange
        DeliveryOptionPayload deliveryOptionPayload = new DeliveryOptionPayload();
        DeliveryOptionResponse expected = new DeliveryOptionResponse();
        DeliveryOption deliveryOption = new DeliveryOption();
        when(deliveryOptionConverter.toBusiness(deliveryOptionPayload)).thenReturn(deliveryOption);
        when(deliveryOptionConverter.toResponse(deliveryOption)).thenReturn(expected);

        // act
        DeliveryOptionResponse result = deliveryOptionService.update(0, deliveryOptionPayload);

        // assert
        verify(deliveryOptionMapper, times(1)).update(deliveryOption);
        assertEquals(expected, result);
    }
}

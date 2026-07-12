package com.ebock.DeliveryOption;

import com.ebock.business.DeliveryOption;
import com.ebock.converter.DeliveryOptionConverter;
import com.ebock.dto.response.deliveryOption.DeliveryOptionResponse;
import com.ebock.mapper.DeliveryOptionMapper;
import com.ebock.service.DeliveryOptionService;
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
public class DeliveryOptionServiceTest {

    @Mock
    DeliveryOptionMapper deliveryOptionMapper;

    @Mock
    DeliveryOptionConverter deliveryOptionConverter;

    @InjectMocks
    DeliveryOptionService deliveryOptionService;

    @Test
    void testListReturnsDeliveryOptionList() {
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
    void testDeleteCallsDeleteAndReturnsResult() {
        // arrange
        int deliveryOptnId = 0;
        // act
        Response result = deliveryOptionService.delete(deliveryOptnId);

        // assert
        verify(deliveryOptionMapper, times(1)).delete(deliveryOptnId);
        assertEquals(204, result.getStatus());
    }
}

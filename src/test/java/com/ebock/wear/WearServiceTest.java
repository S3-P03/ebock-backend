package com.ebock.wear;

import com.ebock.business.Tag;
import com.ebock.business.Wear;
import com.ebock.converter.TagConverter;
import com.ebock.converter.WearConverter;
import com.ebock.dto.request.tag.TagPayload;
import com.ebock.dto.request.wear.WearPayload;
import com.ebock.dto.response.tag.TagResponse;
import com.ebock.dto.response.wear.WearResponse;
import com.ebock.mapper.TagMapper;
import com.ebock.mapper.WearMapper;
import com.ebock.service.TagService;
import com.ebock.service.WearService;
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
public class WearServiceTest {
    @Mock
    WearMapper wearMapper;
    @Mock
    WearConverter wearConverter;
    @InjectMocks
    WearService wearService;

    @Test
    void testListReturnsTagList() {
        // arrange
        List<Wear> wears = new ArrayList<>();
        List<WearResponse> expected = new ArrayList<>();
        when(wearMapper.getAllWears()).thenReturn(wears);
        when(wearConverter.toResponse(wears)).thenReturn(expected);

        // act
        List<WearResponse> result = wearService.list();

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertCallsInsertAndReturnsCreatedObject() {
        // arrange
        WearPayload wearPayload = new WearPayload();
        WearResponse expected = new WearResponse();
        Wear wear = new Wear();
        when(wearConverter.toBusiness(wearPayload)).thenReturn(wear);
        when(wearConverter.toResponse(wear)).thenReturn(expected);

        // act
        WearResponse result = wearService.insert(wearPayload);

        // assert
        verify(wearMapper, times(1)).insert(wear);
        assertEquals(expected, result);
    }

    @Test
    void testUpdateCallsUpdateAndReturnsUpdatedOject() {
        // arrange
        WearPayload wearPayload = new WearPayload();
        WearResponse expected = new WearResponse();
        Wear wear = new Wear();
        when(wearConverter.toBusiness(wearPayload)).thenReturn(wear);
        when(wearConverter.toResponse(wear)).thenReturn(expected);

        // act
        WearResponse result = wearService.update(0, wearPayload);

        // assert
        verify(wearMapper, times(1)).update(wear);
        assertEquals(expected, result);
    }
}

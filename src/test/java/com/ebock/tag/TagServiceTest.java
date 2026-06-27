package com.ebock.tag;

import com.ebock.business.Tag;
import com.ebock.converter.TagConverter;
import com.ebock.dto.request.tag.TagPayload;
import com.ebock.dto.response.tag.TagResponse;
import com.ebock.mapper.TagMapper;
import com.ebock.service.TagService;
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
public class TagServiceTest {

    @Mock
    TagMapper tagMapper;

    @Mock
    TagConverter tagConverter;

    @InjectMocks
    TagService tagService;

    @Test
    void testListReturnsTagList() {
        // arrange
        List<Tag> tags = new ArrayList<>();
        List<TagResponse> expected = new ArrayList<>();
        when(tagMapper.getAllTags()).thenReturn(tags);
        when(tagConverter.toResponse(tags)).thenReturn(expected);

        // act
        List<TagResponse> result = tagService.list();

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testInsertCallsInsertAndReturnsCreatedObject() {
        // arrange
        TagPayload tagPayload = new TagPayload();
        TagResponse expected = new TagResponse();
        Tag tag = new Tag();
        when(tagConverter.toBusiness(tagPayload)).thenReturn(tag);
        when(tagConverter.toResponse(tag)).thenReturn(expected);

        // act
        TagResponse result = tagService.insert(tagPayload);

        // assert
        verify(tagMapper, times(1)).insert(tag);
        assertEquals(expected, result);
    }

    @Test
    void testUpdateCallsUpdateAndReturnsUpdatedOject() {
        // arrange
        TagPayload tagPayload = new TagPayload();
        TagResponse expected = new TagResponse();
        Tag tag = new Tag();
        when(tagConverter.toBusiness(tagPayload)).thenReturn(tag);
        when(tagConverter.toResponse(tag)).thenReturn(expected);

        // act
        TagResponse result = tagService.update(0, tagPayload);

        // assert
        verify(tagMapper, times(1)).update(tag);
        assertEquals(expected, result);
    }
}

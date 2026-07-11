package com.ebock.comment;

import com.ebock.mapper.CommentMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.service.CommentService;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    ItemMapper itemMapper;
    @Mock
    CommentMapper commentMapper;

    @InjectMocks
    CommentService commentService;

    @Test
    void commentDelete_Deletes_() {
        Mockito.when(commentMapper.getCountById(1)).thenReturn(1);
        // act
        Response response = commentService.delete(1);

        // assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        verify(commentMapper).delete(1);
    }

    @Test
    void commentDelete_ThrowsNotFound_InexistentComment() {
        //arrange
        Integer inexistentId = 111;
        Mockito.when(commentMapper.getCountById(inexistentId)).thenReturn(0);
        // act
        assertThrows(NotFoundException.class, () -> {
            commentService.delete(inexistentId);
        });
    }
}

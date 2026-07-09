package com.ebock.comment;

import com.ebock.dto.request.comment.CommentPayload;
import com.ebock.dto.response.comment.CommentDetailsResponse;
import com.ebock.mapper.CommentMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.service.CommentService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    ItemMapper itemMapper;
    @Mock
    CommentMapper commentMapper;
    @Mock
    SecurityContext securityContext;
    @Mock
    Principal principal;

    @InjectMocks
    CommentService commentService;

    @Test
    void commentDetails_ThrowsNotFound_InexistentItemId(){
        //arrange
        int inexistentId = 10;
        when(itemMapper.getItemCountById(inexistentId)).thenReturn(0);

        //act and assert
        assertThrows(NotFoundException.class, () -> {
            commentService.idDetailsComment(inexistentId);
        });
    }

    @Test
    void commentDetails_Works_ExistentItemId(){
        //arrange
        int validId = 1;
        List<CommentDetailsResponse> expected = new ArrayList<>();
        when(itemMapper.getItemCountById(validId)).thenReturn(1);
        when(commentMapper.getDetailledComments(validId)).thenReturn(expected);

        //act
        List<CommentDetailsResponse> result = commentService.idDetailsComment(validId);

        //assert
        assertEquals(expected, result);
    }

    @Test
    void commentInsert_ThrowsNotFound_InexistentItemId() {

        // arrange
        int inexistentId = 10;
        CommentPayload payload = new CommentPayload();

        when(itemMapper.getItemCountById(inexistentId)).thenReturn(0);
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("pele3157");

        // act + assert
        assertThrows(NotFoundException.class,
                () -> commentService.insert(inexistentId, payload));

        verify(commentMapper, never()).insert(anyInt(), anyString(), any());
    }

    @Test
    void commentInsert_Inserts_ExistentItemId() {

        // arrange
        int validId = 1;
        String cip = "pele3157";
        CommentPayload payload = new CommentPayload();

        when(itemMapper.getItemCountById(validId)).thenReturn(1);
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(cip);

        // act
        Response response = commentService.insert(validId, payload);

        // assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        verify(commentMapper).insert(validId, cip, payload);
    }

    @Test
    void commentDelete_ThrowsNotFound_InexistentItemId() {

        // arrange
        int inexistentId = 10;
        when(itemMapper.getItemCountById(inexistentId)).thenReturn(0);

        // act + assert
        assertThrows(NotFoundException.class, () -> commentService.delete(inexistentId));

        verify(commentMapper, never()).delete(anyInt());
    }

    @Test
    void commentDelete_Deletes_ExistentItemId() {

        // arrange
        int validId = 1;
        when(itemMapper.getItemCountById(validId)).thenReturn(1);

        // act
        Response response = commentService.delete(validId);

        // assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        verify(commentMapper).delete(validId);
    }
}

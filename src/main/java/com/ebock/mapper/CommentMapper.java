package com.ebock.mapper;

import com.ebock.dto.request.comment.CommentPayload;
import com.ebock.dto.response.comment.CommentDetailsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<CommentDetailsResponse> getDetailledComments(@Param("id") Integer id);
    void insert(@Param("id") Integer id,
                @Param("cip") String cip,
                @Param("comment") CommentPayload comment);
    void delete(@Param("id") Integer id);
}

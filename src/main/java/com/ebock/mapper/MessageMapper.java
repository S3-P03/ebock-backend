package com.ebock.mapper;

import com.ebock.dto.response.message.MessageResponse;
import com.ebock.dto.response.message.RoomDetailsResponse;
import com.ebock.dto.response.message.RoomResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<MessageResponse> getAllRoomMessages(@Param("id") int id);
    RoomDetailsResponse getRoomInformation(@Param("id") int id);
    RoomResponse createRoom(@Param("itemId") int itemId, @Param("cip") String cip);
    MessageResponse insert(@Param("content") String content, @Param("cip") String cip, @Param("roomId") int roomId);
    int getRoomCountById(@Param("id") int id);
}

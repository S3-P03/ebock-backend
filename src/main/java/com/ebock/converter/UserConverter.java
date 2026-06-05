package com.ebock.converter;

import com.ebock.business.User;
import com.ebock.dto.response.user.ForeignUserResponse;
import com.ebock.dto.response.user.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserConverter {
    UserResponse toResponse(User user);
    ForeignUserResponse toForeignUserResponse(User user);
}
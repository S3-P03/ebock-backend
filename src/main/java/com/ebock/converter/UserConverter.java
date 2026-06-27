package com.ebock.converter;

import com.ebock.business.User;
import com.ebock.dto.response.user.ListUtilisateursUserResponse;
import com.ebock.dto.response.user.ProfileUserResponse;
import com.ebock.dto.response.user.SellerUserResponse;
import com.ebock.dto.response.user.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface UserConverter {
    UserResponse toResponse(User user);
    SellerUserResponse toSellerUserResponse(User user);
    ProfileUserResponse toProfileUserResponse(User user);

    @Mapping(source = "username", target = "cip")
    ListUtilisateursUserResponse toListUtilisateursUserResponse(UserRepresentation userRepresentation);
    List<ListUtilisateursUserResponse> toResponseFromUserRepresentation(List<UserRepresentation> userRepresentations);
}
package com.ebock.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public class EditProfilePicturePayload {
    @NotBlank
    public String guid;
}

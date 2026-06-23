package com.ebock.dto.request.user;

import jakarta.validation.constraints.NotNull;

public class UserChangePasswordPayload {
    @NotNull
    public String oldPassword;
    @NotNull
    public String newPassword;
}

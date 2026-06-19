package com.ebock.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessagePayload {
    @NotBlank
    @Size(max=500)
    public String content;
    @NotBlank
    @Size(min=8, max=8)
    public String senderCip;
}

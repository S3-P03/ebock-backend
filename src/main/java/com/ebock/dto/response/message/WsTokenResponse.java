package com.ebock.dto.response.message;

public record WsTokenResponse (
    String token,
    int expiresInSeconds
) {}

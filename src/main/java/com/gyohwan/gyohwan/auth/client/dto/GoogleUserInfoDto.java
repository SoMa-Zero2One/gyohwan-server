package com.gyohwan.gyohwan.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfoDto(
        @JsonProperty("sub") String sub,
        @JsonProperty("name") String name,
        @JsonProperty("picture") String picture,
        @JsonProperty("email") String email,
        @JsonProperty("email_verified") Boolean emailVerified

) {
}

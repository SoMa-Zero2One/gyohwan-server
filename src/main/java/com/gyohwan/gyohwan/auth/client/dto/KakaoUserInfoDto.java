package com.gyohwan.gyohwan.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoDto(
        @JsonProperty("id") String id,
        @JsonProperty("connected_at") String connectedAt,
        @JsonProperty("kakao_account") KakaoAccountDto kakaoAccountDto

) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccountDto(
            @JsonProperty("profile") KakaoProfileDto profile,
            String email) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record KakaoProfileDto(
                @JsonProperty("profile_image_url") String profileImageUrl,
                @JsonProperty("nickname") String nickname
        ) {
        }
    }
}

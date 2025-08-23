package com.gyohwan.compass.legacyYu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTriggerService {

    private final RestTemplate restTemplate;

    @Value("${email.trigger.url:http://43.200.5.166:8000/send-email}")
    private String emailTriggerUrl;

    /**
     * 신규 유저 등록 시 이메일 알람 트리거
     */
    public void triggerNewUserRegistrationEmail(String email, String uuid, String nickname, String collegeName) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("to_address", email);
            payload.put("nickname", nickname);
            payload.put("uuid", uuid);
            payload.put("college_name", collegeName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            restTemplate.exchange(
                    emailTriggerUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("이메일 트리거 요청 성공 - Email: {}, Nickname: {}", email, nickname);

        } catch (Exception e) {
            log.error("이메일 트리거 요청 실패 - Email: {}, Error: {}", email, e.getMessage());
            // 이메일 트리거 실패가 전체 등록 프로세스를 방해하지 않도록 예외를 던지지 않음
        }
    }
}

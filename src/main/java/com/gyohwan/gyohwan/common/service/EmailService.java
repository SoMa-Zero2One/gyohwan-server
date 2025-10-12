package com.gyohwan.gyohwan.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to); // 수신자
        message.setSubject("[교환닷컴] 회원가입 이메일 인증"); // 메일 제목

        String text = "인증 코드는 " + verificationCode + " 입니다.";
        message.setText(text); // 메일 본문

        javaMailSender.send(message);
    }
}
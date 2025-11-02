package com.gyohwan.gyohwan.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public void sendPasswordResetEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[교환닷컴] 비밀번호 재설정 인증");

        String text = String.format(
                "비밀번호 재설정 인증 코드는 %s 입니다.\n\n" +
                "인증 코드는 10분간 유효합니다.\n" +
                "본인이 요청하지 않은 경우 이 이메일을 무시하세요.",
                verificationCode
        );
        message.setText(text);

        javaMailSender.send(message);
        log.info("비밀번호 재설정 이메일 발송 완료. Email: {}", to);
    }
}
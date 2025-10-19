package com.gyohwan.gyohwan.compare.service;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class NicknameGenerator {

    private static final List<String> ADJECTIVES = Arrays.asList(
            "맛있는", "귀여운", "멋진", "행복한", "즐거운",
            "신나는", "예쁜", "착한", "똑똑한", "용감한",
            "재미있는", "활발한", "차분한", "따뜻한", "시원한",
            "달콤한", "상큼한", "부드러운", "강한", "빠른"
    );

    private static final List<String> NOUNS = Arrays.asList(
            "사과", "바나나", "포도", "딸기", "수박",
            "복숭아", "자두", "감", "배", "귤",
            "토끼", "고양이", "강아지", "햄스터", "앵무새",
            "판다", "코알라", "펭귄", "돌고래", "나비"
    );

    private final Random random = new Random();

    public String generate() {
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(random.nextInt(NOUNS.size()));
        int number = random.nextInt(1000); // 0-999
        return adjective + " " + noun + String.format("%03d", number);
    }
}


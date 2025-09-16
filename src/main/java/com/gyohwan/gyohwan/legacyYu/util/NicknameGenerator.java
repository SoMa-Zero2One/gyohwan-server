package com.gyohwan.gyohwan.legacyYu.util;

public class NicknameGenerator {

    private static final String[] ADJECTIVES = {
            "용감한", "성공적인", "멋진", "귀여운", "예쁜", "초롱초롱", "신난", "생각하는", "활발한", "감성적인",
            "아리따운", "열정있는", "꼼꼼한", "꿈꾸는", "깜찍한", "행복한", "파이팅하는", "명랑한", "즐거운", "슬기로운",
            "지혜로운", "관대한", "깔끔한", "겸손한", "공손한", "놀라운", "느긋한", "낭만적인", "다정한", "당당한",
            "든든한", "다재다능한", "믿음직한", "맑은", "매력적인", "반듯한", "부드러운", "밝은", "순한", "선한",
            "시원시원한", "섬세한", "성실한", "착한", "친절한", "차분한", "편안한", "합리적인"
    };

    private static final String[] NOUNS = {
            "강아지", "고양이", "토끼", "사자", "호랑이", "기린", "다람쥐", "참새", "해달", "판다",
            "여우", "곰", "코끼리", "돌고래", "수달", "상어", "펭귄", "캥거루", "사슴", "알파카",
            "오리", "너구리", "거북이", "개구리", "문어", "쿼카"
    };

    /**
     * 사용자 ID를 기반으로 고유한 닉네임을 생성
     * 형용사 + 명사 조합으로 생성
     */
    public static String generateNickname(Long userId) {
        int adjectiveIndex = (int) (userId % ADJECTIVES.length);
        int nounIndex = (int) (userId % NOUNS.length);

        String adjective = ADJECTIVES[adjectiveIndex];
        String noun = NOUNS[nounIndex];

        return adjective + " " + noun;
    }

    /**
     * 랜덤 닉네임 생성 (하위 호환성을 위해 유지)
     *
     * @deprecated generateNickname(Long userId) 사용을 권장
     */
    @Deprecated
    public static String generateRandomNickname() {
        // 현재 시간을 기반으로 임시 ID 생성
        long tempId = System.currentTimeMillis();
        return generateNickname(tempId);
    }
}

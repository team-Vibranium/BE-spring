package com.bespring.domain.alarm.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoiceType {

    ALLOY("alloy", "균형 잡힌 중성적 목소리"),
    ASH("ash", "부드럽고 따뜻한 목소리"),
    BALLAD("ballad", "차분하고 서정적인 목소리"),
    CORAL("coral", "밝고 친근한 목소리"),
    ECHO("echo", "깊고 울림이 있는 목소리"),
    SAGE("sage", "지혜롭고 안정적인 목소리"),
    SHIMMER("shimmer", "반짝이는 활기찬 목소리"),
    VERSE("verse", "리드미컬하고 표현력 좋은 목소리");

    private final String value;
    private final String description;

    // OpenAI API에 전송할 값 반환
    public String getApiValue() {
        return this.value;
    }

    // 문자열로부터 VoiceType 찾기
    public static VoiceType fromValue(String value) {
        for (VoiceType voiceType : values()) {
            if (voiceType.getValue().equals(value)) {
                return voiceType;
            }
        }
        throw new IllegalArgumentException("Unknown voice type: " + value);
    }

    // 모든 음성 타입의 값 배열 반환
    public static String[] getAllValues() {
        return java.util.Arrays.stream(values())
                .map(VoiceType::getValue)
                .toArray(String[]::new);
    }
}
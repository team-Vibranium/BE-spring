package com.bespring.domain.alarm.entity;

import com.bespring.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_alarms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "alarm_time", nullable = false)
    private LocalDateTime alarmTime;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(name = "voice", nullable = false, length = 50)
    private VoiceType voice;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 팩토리 메서드
    public static UserAlarm create(User user, LocalDateTime alarmTime, String instructions, VoiceType voice) {
        return UserAlarm.builder()
                .user(user)
                .alarmTime(alarmTime)
                .instructions(instructions)
                .voice(voice)
                .active(true)
                .build();
    }

    // 비즈니스 메서드
    public void deactivate() {
        this.active = false;
    }

    public void updateAlarm(LocalDateTime alarmTime, String instructions, VoiceType voice) {
        this.alarmTime = alarmTime;
        this.instructions = instructions;
        this.voice = voice;
    }

    public boolean isActive() {
        return this.active;
    }

    // 스누즈 횟수가 있을 때 instructions에 추가하는 메서드
    public String getInstructionsWithSnooze(int snoozeCount) {
        if (snoozeCount == 0) {
            return this.instructions;
        }

        String snoozeMessage = String.format(
            "\n\n[중요] 사용자가 이미 %d번 스누즈했습니다. 더 적극적으로 깨워주세요!",
            snoozeCount
        );

        return this.instructions + snoozeMessage;
    }
}
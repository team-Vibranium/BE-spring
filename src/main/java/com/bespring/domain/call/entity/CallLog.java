package com.bespring.domain.call.entity;

import com.bespring.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "call_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "call_start")
    private LocalDateTime callStart;

    @Column(name = "call_end")
    private LocalDateTime callEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallResult result;

    @Column(name = "snooze_count", nullable = false)
    @Builder.Default
    private Integer snoozeCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum CallResult {
        SUCCESS,
        FAIL_NO_TALK,
        FAIL_SNOOZE
    }

    public boolean isSuccessful() {
        return result == CallResult.SUCCESS;
    }
}
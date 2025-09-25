package com.bespring.domain.points.entity;

import com.bespring.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "points_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type;

    @Column(nullable = false)
    private Integer amount;

    @Column(length = 200)
    private String description;

    @Column(name = "related_alarm_id", length = 100)
    private String relatedAlarmId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum PointType {
        GRADE,      // 등급 포인트 (알람 성공 보상)
        CONSUMPTION // 소비 포인트 (아바타 구매 등)
    }

    public boolean isEarned() {
        return amount > 0;
    }

    public boolean isSpent() {
        return amount < 0;
    }
}
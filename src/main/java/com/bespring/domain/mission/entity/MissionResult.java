package com.bespring.domain.mission.entity;

import com.bespring.domain.call.entity.CallLog;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_log_id", nullable = false)
    private CallLog callLog;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false)
    private MissionType missionType;

    @Column(nullable = false)
    private Boolean success;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum MissionType {
        PUZZLE,
        MATH,
        MEMORY,
        QUIZ
    }

    public boolean isSuccessful() {
        return success != null && success;
    }
}
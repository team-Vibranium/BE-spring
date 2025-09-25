package com.bespring.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 50)
    private String nickname;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    @Column(name = "selected_avatar", length = 50)
    @Builder.Default
    private String selectedAvatar = "avatar_1";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addPoints(Integer amount) {
        this.points += amount;
    }

    public void subtractPoints(Integer amount) {
        if (this.points >= amount) {
            this.points -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient points");
        }
    }
}
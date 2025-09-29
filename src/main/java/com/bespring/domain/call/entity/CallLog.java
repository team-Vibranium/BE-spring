package com.bespring.domain.call.entity;

import com.bespring.domain.call.dto.Utterance;
import com.bespring.domain.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "call_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
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

    @Column(name = "conversation_data", columnDefinition = "JSON")
    private String conversationData;

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

    public static CallLog createWithConversation(User user, LocalDateTime callStart,
                                               LocalDateTime callEnd, CallResult result,
                                               int snoozeCount, String conversationData) {
        return CallLog.builder()
                .user(user)
                .callStart(callStart)
                .callEnd(callEnd)
                .result(result)
                .snoozeCount(snoozeCount)
                .conversationData(conversationData)
                .build();
    }

    public static CallLog createBasic(User user, LocalDateTime callStart, CallResult result) {
        return CallLog.builder()
                .user(user)
                .callStart(callStart)
                .result(result)
                .snoozeCount(0)
                .build();
    }

    public List<Utterance> getConversationList() {
        if (conversationData == null || conversationData.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(conversationData, new TypeReference<List<Utterance>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse conversation data for CallLog id: {}", id, e);
            return new ArrayList<>();
        }
    }

    public static CallLog createWithConversationList(User user, LocalDateTime callStart,
                                                   LocalDateTime callEnd, CallResult result,
                                                   int snoozeCount, List<Utterance> utterances) {
        String conversationJson = convertUtterancesToJson(utterances);
        return CallLog.builder()
                .user(user)
                .callStart(callStart)
                .callEnd(callEnd)
                .result(result)
                .snoozeCount(snoozeCount)
                .conversationData(conversationJson)
                .build();
    }

    private static String convertUtterancesToJson(List<Utterance> utterances) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(utterances);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert utterances to JSON", e);
            return "[]";
        }
    }
}
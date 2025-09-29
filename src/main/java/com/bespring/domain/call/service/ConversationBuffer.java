package com.bespring.domain.call.service;

import com.bespring.domain.call.dto.Utterance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationBuffer {

    private final ObjectMapper objectMapper;
    private final Map<String, List<Utterance>> sessionBuffers = new ConcurrentHashMap<>();

    public void addUserUtterance(String sessionId, String text) {
        List<Utterance> utterances = sessionBuffers.computeIfAbsent(sessionId, k -> new ArrayList<>());
        utterances.add(Utterance.createUser(text));
        log.debug("Added user utterance to session {}: {}", sessionId, text);
    }

    public void addAssistantUtterance(String sessionId, String text) {
        List<Utterance> utterances = sessionBuffers.computeIfAbsent(sessionId, k -> new ArrayList<>());
        utterances.add(Utterance.createAssistant(text));
        log.debug("Added assistant utterance to session {}: {}", sessionId, text);
    }

    public void addSystemMessage(String sessionId, String message) {
        List<Utterance> utterances = sessionBuffers.computeIfAbsent(sessionId, k -> new ArrayList<>());
        utterances.add(Utterance.createSystem(message));
        log.debug("Added system message to session {}: {}", sessionId, message);
    }

    public String getConversationJson(String sessionId) {
        List<Utterance> utterances = sessionBuffers.getOrDefault(sessionId, new ArrayList<>());
        try {
            return objectMapper.writeValueAsString(utterances);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert conversation to JSON for session: {}", sessionId, e);
            return "[]";
        }
    }

    public List<Utterance> getConversationList(String sessionId) {
        return new ArrayList<>(sessionBuffers.getOrDefault(sessionId, new ArrayList<>()));
    }

    public void clearSession(String sessionId) {
        sessionBuffers.remove(sessionId);
        log.debug("Cleared conversation buffer for session: {}", sessionId);
    }

    public int getUtteranceCount(String sessionId) {
        return sessionBuffers.getOrDefault(sessionId, new ArrayList<>()).size();
    }

    public boolean hasSession(String sessionId) {
        return sessionBuffers.containsKey(sessionId);
    }
}
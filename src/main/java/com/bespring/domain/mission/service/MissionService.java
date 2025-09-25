package com.bespring.domain.mission.service;

import com.bespring.domain.mission.entity.MissionResult;

import java.util.List;

public interface MissionService {

    MissionResult saveMissionResult(Long callLogId, MissionResult.MissionType missionType, boolean success);

    List<MissionResult> getMissionResults(Long callLogId);

    Long getTotalMissionCount(Long userId);

    Long getSuccessMissionCount(Long userId);

    Double getMissionSuccessRate(Long userId);

    Long getMissionCountByType(Long userId, MissionResult.MissionType missionType);

    Long getSuccessMissionCountByType(Long userId, MissionResult.MissionType missionType);
}
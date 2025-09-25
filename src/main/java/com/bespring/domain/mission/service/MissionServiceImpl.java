package com.bespring.domain.mission.service;

import com.bespring.domain.call.entity.CallLog;
import com.bespring.domain.call.service.CallLogService;
import com.bespring.domain.mission.entity.MissionResult;
import com.bespring.domain.mission.repository.MissionResultRepository;
import com.bespring.global.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MissionServiceImpl implements MissionService {

    private final MissionResultRepository missionResultRepository;
    private final CallLogService callLogService;

    @Override
    public MissionResult saveMissionResult(Long callLogId, MissionResult.MissionType missionType, boolean success) {
        CallLog callLog = callLogService.findById(callLogId)
                .orElseThrow(() -> new InvalidRequestException("통화 로그를 찾을 수 없습니다."));

        MissionResult missionResult = MissionResult.builder()
                .callLog(callLog)
                .missionType(missionType)
                .success(success)
                .build();

        MissionResult saved = missionResultRepository.save(missionResult);

        log.info("Mission result saved: callLogId={}, missionType={}, success={}",
                callLogId, missionType, success);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionResult> getMissionResults(Long callLogId) {
        CallLog callLog = callLogService.findById(callLogId)
                .orElseThrow(() -> new InvalidRequestException("통화 로그를 찾을 수 없습니다."));

        // JOIN FETCH를 사용하여 N+1 쿼리 문제 해결
        return missionResultRepository.findByCallLogWithCallLog(callLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalMissionCount(Long userId) {
        return missionResultRepository.countTotalByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSuccessMissionCount(Long userId) {
        return missionResultRepository.countSuccessByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMissionSuccessRate(Long userId) {
        Long totalMissions = getTotalMissionCount(userId);
        if (totalMissions == 0) {
            return 0.0;
        }

        Long successMissions = getSuccessMissionCount(userId);
        return (successMissions.doubleValue() / totalMissions.doubleValue()) * 100.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMissionCountByType(Long userId, MissionResult.MissionType missionType) {
        return missionResultRepository.countTotalByUserIdAndMissionType(userId, missionType);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSuccessMissionCountByType(Long userId, MissionResult.MissionType missionType) {
        return missionResultRepository.countSuccessByUserIdAndMissionType(userId, missionType);
    }
}
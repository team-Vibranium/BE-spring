package com.bespring.domain.mission.repository;

import com.bespring.domain.call.entity.CallLog;
import com.bespring.domain.mission.entity.MissionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionResultRepository extends JpaRepository<MissionResult, Long> {

    List<MissionResult> findByCallLog(CallLog callLog);

    @Query("SELECT m FROM MissionResult m JOIN FETCH m.callLog WHERE m.callLog = :callLog")
    List<MissionResult> findByCallLogWithCallLog(@Param("callLog") CallLog callLog);

    Optional<MissionResult> findByCallLogAndMissionType(CallLog callLog, MissionResult.MissionType missionType);

    @Query("SELECT m FROM MissionResult m JOIN FETCH m.callLog WHERE m.callLog = :callLog AND m.missionType = :missionType")
    Optional<MissionResult> findByCallLogAndMissionTypeWithCallLog(@Param("callLog") CallLog callLog, @Param("missionType") MissionResult.MissionType missionType);

    @Query("SELECT COUNT(m) FROM MissionResult m JOIN m.callLog c WHERE c.user.id = :userId AND m.success = true")
    Long countSuccessByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MissionResult m JOIN m.callLog c WHERE c.user.id = :userId")
    Long countTotalByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MissionResult m JOIN m.callLog c WHERE c.user.id = :userId AND m.missionType = :missionType AND m.success = true")
    Long countSuccessByUserIdAndMissionType(@Param("userId") Long userId, @Param("missionType") MissionResult.MissionType missionType);

    @Query("SELECT COUNT(m) FROM MissionResult m JOIN m.callLog c WHERE c.user.id = :userId AND m.missionType = :missionType")
    Long countTotalByUserIdAndMissionType(@Param("userId") Long userId, @Param("missionType") MissionResult.MissionType missionType);

    @Query("SELECT m FROM MissionResult m JOIN m.callLog c WHERE c.user.id = :userId ORDER BY m.createdAt DESC")
    List<MissionResult> findRecentByUserId(@Param("userId") Long userId);
}
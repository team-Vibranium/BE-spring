package com.bespring.domain.points.service;

import com.bespring.domain.points.dto.request.PointsEarnRequest;
import com.bespring.domain.points.dto.request.PointsSpendRequest;
import com.bespring.domain.points.dto.response.PointsSummaryResponse;
import com.bespring.domain.points.entity.PointsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointsService {

    PointsSummaryResponse getPointsSummary(Long userId);

    Page<PointsHistory> getPointsHistory(Long userId, PointsHistory.PointType type, Pageable pageable);

    PointsHistory earnPoints(Long userId, PointsEarnRequest request);

    PointsHistory spendPoints(Long userId, PointsSpendRequest request);

    Integer getTotalPoints(Long userId);

    Integer getPointsByType(Long userId, PointsHistory.PointType type);

    String calculateGrade(Integer gradePoints);
}
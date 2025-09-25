package com.bespring.domain.points.service;

import com.bespring.domain.points.dto.request.PointsEarnRequest;
import com.bespring.domain.points.dto.request.PointsSpendRequest;
import com.bespring.domain.points.dto.response.PointsSummaryResponse;
import com.bespring.domain.points.entity.PointsHistory;
import com.bespring.domain.points.repository.PointsHistoryRepository;
import com.bespring.domain.user.entity.User;
import com.bespring.domain.user.repository.UserRepository;
import com.bespring.global.exception.InsufficientPointsException;
import com.bespring.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PointsServiceImpl implements PointsService {

    private final PointsHistoryRepository pointsHistoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PointsSummaryResponse getPointsSummary(Long userId) {
        User user = getUserById(userId);

        Integer consumptionPoints = getPointsByType(userId, PointsHistory.PointType.CONSUMPTION);
        Integer gradePoints = getPointsByType(userId, PointsHistory.PointType.GRADE);
        String currentGrade = calculateGrade(gradePoints);

        return PointsSummaryResponse.of(consumptionPoints, gradePoints, currentGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointsHistory> getPointsHistory(Long userId, PointsHistory.PointType type, Pageable pageable) {
        User user = getUserById(userId);

        // JOIN FETCH를 사용하여 N+1 쿼리 문제 해결
        if (type != null) {
            return pointsHistoryRepository.findByUserAndTypeWithUserOrderByCreatedAtDesc(user, type, pageable);
        } else {
            return pointsHistoryRepository.findByUserWithUserOrderByCreatedAtDesc(user, pageable);
        }
    }

    @Override
    public PointsHistory earnPoints(Long userId, PointsEarnRequest request) {
        User user = getUserById(userId);

        // 포인트 히스토리 생성
        PointsHistory pointsHistory = PointsHistory.builder()
                .user(user)
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .relatedAlarmId(request.getRelatedAlarmId())
                .build();

        // 사용자 포인트 업데이트
        user.addPoints(request.getAmount());
        userRepository.save(user);

        PointsHistory saved = pointsHistoryRepository.save(pointsHistory);

        log.info("Points earned: userId={}, amount={}, type={}, newTotal={}",
                userId, request.getAmount(), request.getType(), user.getPoints());

        return saved;
    }

    @Override
    public PointsHistory spendPoints(Long userId, PointsSpendRequest request) {
        User user = getUserById(userId);

        // 포인트 충분한지 확인 (소비 포인트만)
        if (request.getType() == PointsHistory.PointType.CONSUMPTION) {
            Integer consumptionPoints = getPointsByType(userId, PointsHistory.PointType.CONSUMPTION);
            if (consumptionPoints < request.getAmount()) {
                throw new InsufficientPointsException("소비 포인트가 부족합니다.");
            }
        }

        // 포인트 히스토리 생성 (음수로 저장)
        PointsHistory pointsHistory = PointsHistory.builder()
                .user(user)
                .type(request.getType())
                .amount(-request.getAmount())
                .description(request.getDescription())
                .build();

        // 사용자 포인트 업데이트
        user.subtractPoints(request.getAmount());
        userRepository.save(user);

        PointsHistory saved = pointsHistoryRepository.save(pointsHistory);

        log.info("Points spent: userId={}, amount={}, type={}, newTotal={}",
                userId, request.getAmount(), request.getType(), user.getPoints());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalPoints(Long userId) {
        User user = getUserById(userId);
        return user.getPoints();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getPointsByType(Long userId, PointsHistory.PointType type) {
        User user = getUserById(userId);
        Integer points = pointsHistoryRepository.sumAmountByUserAndType(user, type);
        return points != null ? points : 0;
    }

    @Override
    public String calculateGrade(Integer gradePoints) {
        if (gradePoints == null || gradePoints < 100) {
            return "BRONZE";
        } else if (gradePoints < 500) {
            return "SILVER";
        } else if (gradePoints < 1000) {
            return "GOLD";
        } else if (gradePoints < 2000) {
            return "PLATINUM";
        } else {
            return "DIAMOND";
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
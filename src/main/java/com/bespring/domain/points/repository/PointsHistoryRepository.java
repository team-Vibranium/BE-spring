package com.bespring.domain.points.repository;

import com.bespring.domain.points.entity.PointsHistory;
import com.bespring.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {

    Page<PointsHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<PointsHistory> findByUserAndTypeOrderByCreatedAtDesc(User user, PointsHistory.PointType type, Pageable pageable);

    @Query("SELECT p FROM PointsHistory p JOIN FETCH p.user WHERE p.user = :user ORDER BY p.createdAt DESC")
    Page<PointsHistory> findByUserWithUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM PointsHistory p JOIN FETCH p.user WHERE p.user = :user AND p.type = :type ORDER BY p.createdAt DESC")
    Page<PointsHistory> findByUserAndTypeWithUserOrderByCreatedAtDesc(
            @Param("user") User user, @Param("type") PointsHistory.PointType type, Pageable pageable
    );

    @Query("SELECT SUM(p.amount) FROM PointsHistory p WHERE p.user = :user AND p.type = :type")
    Integer sumAmountByUserAndType(@Param("user") User user, @Param("type") PointsHistory.PointType type);

    @Query("SELECT SUM(p.amount) FROM PointsHistory p WHERE p.user = :user AND p.type = :type AND p.createdAt >= :startDate")
    Integer sumAmountByUserAndTypeAfterDate(@Param("user") User user, @Param("type") PointsHistory.PointType type, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(p) FROM PointsHistory p WHERE p.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT p FROM PointsHistory p JOIN FETCH p.user WHERE p.user = :user AND p.createdAt >= :startDate AND p.createdAt < :endDate ORDER BY p.createdAt DESC")
    Page<PointsHistory> findByUserAndDateRangeWithUserOrderByCreatedAtDesc(
            @Param("user") User user, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate, Pageable pageable
    );

    @Query("SELECT p FROM PointsHistory p WHERE p.user = :user AND p.createdAt >= :startDate AND p.createdAt < :endDate ORDER BY p.createdAt DESC")
    Page<PointsHistory> findByUserAndDateRangeOrderByCreatedAtDesc(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
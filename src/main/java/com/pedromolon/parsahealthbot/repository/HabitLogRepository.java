package com.pedromolon.parsahealthbot.repository;

import com.pedromolon.parsahealthbot.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    @Query("SELECT hl FROM HabitLog hl WHERE hl.userAccount.chatId = :chatId AND hl.completedAt >= :startOfDay AND hl.completedAt <= :endOfDay")
    List<HabitLog> findTodayLogsByUser(
            @Param("chatId") Long chatId,
            @Param("startOfDay") OffsetDateTime startOfDay,
            @Param("endOfDay") OffsetDateTime endOfDay
    );

}

package com.pedromolon.parsahealthbot.repository;

import com.pedromolon.parsahealthbot.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    @Query("SELECT h FROM Habit h WHERE h.userAccount.chatId = :chatId AND h.active = true")
    List<Habit> findActiveHabitsByChatId(@Param("chatId") Long chatId);
}

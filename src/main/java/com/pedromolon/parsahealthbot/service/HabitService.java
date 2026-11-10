package com.pedromolon.parsahealthbot.service;

import com.pedromolon.parsahealthbot.model.Habit;
import com.pedromolon.parsahealthbot.model.HabitLog;
import com.pedromolon.parsahealthbot.model.UserAccount;
import com.pedromolon.parsahealthbot.repository.HabitLogRepository;
import com.pedromolon.parsahealthbot.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserService userService;

    public HabitService(HabitRepository habitRepository, HabitLogRepository habitLogRepository, UserService userService) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.userService = userService;
    }

    @Transactional
    public Habit createHabit(Long chatId, String firstName, String habitName) {
        UserAccount user = userService.createUser(chatId, firstName);

        Habit habit = Habit.builder()
                .userAccount(user)
                .name(habitName)
                .active(true)
                .build();

        return habitRepository.save(habit);
    }

    public List<Habit> getActiveHabits(Long chatId) {
        return habitRepository.findActiveHabitsByChatId(chatId);
    }

    @Transactional
    public HabitLog checkHabit(Long chatId, Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Hábito não encontrado"));

        if (!habit.getUserAccount().getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Você não tem permissão para interagir com com este hábito");
        }

        HabitLog habitLog = HabitLog.builder()
                .habit(habit)
                .userAccount(habit.getUserAccount())
                .completedAt(OffsetDateTime.now())
                .build();

        return habitLogRepository.save(habitLog);
    }

    @Transactional
    public HabitLog checkInHabitByName(Long chatId, String habitName) {
        List<Habit> activeHabits = habitRepository.findActiveHabitsByChatId(chatId);

        Habit habitToComplete = activeHabits.stream()
                        .filter(h -> h.getName().equalsIgnoreCase(habitName.trim()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Hábito não encontrado"));

        HabitLog log = HabitLog.builder()
                .habit(habitToComplete)
                .userAccount(habitToComplete.getUserAccount())
                .completedAt(OffsetDateTime.now())
                .build();

        return habitLogRepository.save(log);
    }

    public List<HabitLog> getTodayLogs(Long chatId) {
        OffsetDateTime startOfDay = OffsetDateTime.now().toLocalDate().atStartOfDay(OffsetDateTime.now().getOffset()).toOffsetDateTime();
        OffsetDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return habitLogRepository.findTodayLogsByUser(chatId, startOfDay, endOfDay);
    }

}

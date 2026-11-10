package com.pedromolon.parsahealthbot.model;

import com.pedromolon.parsahealthbot.model.enums.ActivityLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_user_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAccount {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "NUMERIC(5, 2)")
    private Double weight;

    private Integer height;

    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;

    @Column(name = "daily_water_goal_ml")
    private Integer dailyWaterGoalMl;

    @Column(name = "time_zone", nullable = false)
    private String timeZone = "America/Sao_Paulo";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UserAccount(Long chatId, String userName) {
        this.chatId = chatId;
        this.name = userName;
    }
}

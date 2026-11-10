package com.pedromolon.parsahealthbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_habit_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "completed_at", nullable = false)
    private OffsetDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (this.completedAt == null) {
            this.completedAt = OffsetDateTime.now();
        }
    }

}

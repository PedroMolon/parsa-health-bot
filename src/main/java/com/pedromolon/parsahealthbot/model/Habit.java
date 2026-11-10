package com.pedromolon.parsahealthbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_habit")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private UserAccount userAccount;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAd;

    @PrePersist
    public void prePersist() {
        if (this.active == null) {
            this.active = Boolean.TRUE;
        }
        if (this.createdAd == null) {
            this.createdAd = OffsetDateTime.now();
        }
    }

}

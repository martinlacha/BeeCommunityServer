package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity of apiary
 */
@Entity
@Table(name = "EVENT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {


    /**
     * Unique identification of apiary entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

    @Column(name = "title")
    private String title;

    @Column(name = "activity")
    private String activity;

    @Column(name = "type")
    private String type;

    @Column(name = "notes")
    private String notes;

    @Column(name = "date")
    private LocalDate date;

    @Column (name = "finished")
    private Boolean finished;
}

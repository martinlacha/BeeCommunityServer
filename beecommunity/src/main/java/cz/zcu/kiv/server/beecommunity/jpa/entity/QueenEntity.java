package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.QueenEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity for queen
 */
@Entity
@Table(name = "QUEEN", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueenEntity {

    /**
     * Unique identification of queen entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

    @Column(name = "name")
    private String name;

    @Column(name = "breed")
    private String breed;

    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private QueenEnums.EColor color;

    @Column(name = "queen_hatch")
    private LocalDate queenHatch;

    @Column(name = "notes")
    private String notes;

    @Column(name = "image", length = 1024)
    private byte[] image;
}

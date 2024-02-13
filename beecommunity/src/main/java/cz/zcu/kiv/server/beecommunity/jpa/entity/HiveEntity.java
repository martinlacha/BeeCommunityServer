package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.HiveEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity of hive
 */
@Entity
@Table(name = "HIVE", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HiveEntity {

    /**
     * Unique identification of hive entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "apiary_id")
    private ApiaryEntity apiary;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "queen_id")
    private QueenEntity queen;

    @Column(name = "frame_count")
    private int frameCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private HiveEnums.EColor color;

    @Enumerated(EnumType.STRING)
    @Column(name = "bee_source")
    private HiveEnums.EBeeSource source;

    @Column(name = "date_establishment")
    private LocalDate establishment;

    @Column(name = "notes")
    private String notes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "hive")
    private List<InspectionEntity> inspections;
}

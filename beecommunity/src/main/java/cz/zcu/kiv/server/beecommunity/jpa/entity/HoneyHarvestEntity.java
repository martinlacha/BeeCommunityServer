package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity of honey harvest records
 */
@Entity
@Table(name = "HONEY_HARVEST", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoneyHarvestEntity {

    /**
     * Unique identification of honey harvest entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "harvest_date")
    private LocalDate harvestDate;

    @Column(name = "total_honey_weight")
    private double totalHoneyWeight;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "honeyHarvest", cascade = CascadeType.ALL)
    private List<HiveHarvestInfoEntity> hivesInfo;
}

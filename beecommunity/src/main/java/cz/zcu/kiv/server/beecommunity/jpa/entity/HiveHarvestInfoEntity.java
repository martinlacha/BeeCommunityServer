package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "STRESSORS", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HiveHarvestInfoEntity {

    /**
     * Unique identification of hive harvest info entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "honey_harvest_id")
    private HoneyHarvestEntity honeyHarvest;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hive_id")
    private HiveEntity hive;

    @Column(name = "honey_weight")
    private double honeyWeight;
}

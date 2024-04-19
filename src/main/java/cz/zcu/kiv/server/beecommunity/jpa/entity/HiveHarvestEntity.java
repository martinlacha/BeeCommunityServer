package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of harvest information in single hive
 */
@Entity
@Table(name = "HIVE_HARVEST", schema = "public")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HiveHarvestEntity {

    /**
     * Unique identification of hive harvest info entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "product")
    private InspectionEnums.EHarvestProduct product;

    @Column(name = "quantity")
    private double productQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit")
    private InspectionEnums.EUnitsAndDoses productUnit;

    @Column(name = "super_count")
    private int superCount;

    @Column(name = "frame_count")
    private int frameCount;

    @Override
    public String toString() {
        return "HiveHarvestEntity{" +
                "id=" + id +
                ", product=" + product +
                '}';
    }
}

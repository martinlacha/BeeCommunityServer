package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of feeding information in single hive
 */
@Entity
@Table(name = "HIVE_FEEDING", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HiveFeedingEntity {

    /**
     * Unique identification of hive feeding info entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "food")
    private InspectionEnums.EFoodType food;

    @Enumerated(EnumType.STRING)
    @Column(name = "ratio")
    private InspectionEnums.EFoodRatio ratio;

    @Column(name = "quantity")
    private int foodQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit")
    private InspectionEnums.EUnitsAndDoses foodUnit;

    @Override
    public String toString() {
        return "HiveFeedingEntity{" +
                "id=" + id +
                ", food='" + food + '\'' +
                '}';
    }
}

package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity of harvest information in single hive
 */
@Entity
@Table(name = "HIVE_TREATMENT", schema = "public")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HiveTreatmentEntity {

    /**
     * Unique identification of hive harvest info entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "disease")
    private InspectionEnums.EDisease disease;

    @Column(name = "treatment")
    private String treatment;

    @Column(name = "quantity")
    private double quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "dose")
    private InspectionEnums.EUnitsAndDoses dose;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Override
    public String toString() {
        return "HiveTreatmentEntity{" +
                "id=" + id +
                ", disease='" + disease + '\'' +
                ", treatment='" + treatment + '\'' +
                '}';
    }
}

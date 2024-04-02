package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity of hive inspection with details of state in single hive
 */
@Entity
@Table(name = "HIVE_INSPECTION", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionEntity {

    /**
     * Unique identification of hive inspection entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hive_id")
    private HiveEntity hive;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private InspectionEnums.EType type;

    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather")
    private InspectionEnums.EWeather weather;

    /**
     * Population
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "population")
    private InspectionEnums.EPopulation population;

    /**
     * Food
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "food_storage")
    private InspectionEnums.EFoodStorage foodStorage;

    @Enumerated(EnumType.STRING)
    @Column(name = "sources_nearby")
    private InspectionEnums.ESourceNearby sourceNearby;

    @Enumerated(EnumType.STRING)
    @Column(name = "brood_pattern")
    private InspectionEnums.EBroodPattern broodPattern;

    /**
     * Queen and Brood
     */
    @Column(name = "queen")
    private boolean queen;

    @Column(name = "eggs")
    private boolean eggs;

    @Column(name = "uncapped_brood")
    private boolean uncappedBrood;

    @Column(name = "capped_brood")
    private boolean cappedBrood;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "stressors_id")
    private StressorsEntity stressors;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "treatment_id")
    private HiveTreatmentEntity treatment;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "feeding_id")
    private HiveFeedingEntity feeding;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "harvest_id")
    private HiveHarvestEntity harvest;

    @Enumerated(EnumType.STRING)
    @Column(name = "colony_temperament")
    private InspectionEnums.EColonyTemperament temperament;

    @Column(name = "notes")
    private String notes;

    @Column(name = "inspection_image", length = 1024)
    private byte[] inspectionImage;

    @Column(name = "food_image", length = 1024)
    private byte[] foodImage;

    @Column(name = "population_image", length = 1024)
    private byte[] populationImage;

    @Column(name = "queen_image", length = 1024)
    private byte[] queenImage;

    @Column(name = "brood_image", length = 1024)
    private byte[] broodImage;

    @Column(name = "stressors_image", length = 1024)
    private byte[] stressorsImage;

    @Column(name = "disease_image", length = 1024)
    private byte[] diseaseImage;
}
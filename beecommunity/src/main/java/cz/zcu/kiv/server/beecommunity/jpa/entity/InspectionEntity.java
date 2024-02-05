package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "HIVE_INSPECTION", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionEntity {

    /**
     * Unique identification of hive inspection entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hive_id")
    private HiveEntity hive;

    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather")
    private InspectionEnums.EWeather weather;

    @Column(name = "temperature")
    private int temperature;

    /**
     * Population
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "population")
    private InspectionEnums.EPopulation population;

    @Column(name = "covered_frames")
    private int coveredFrames;

    /**
     * Food
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "food_storage")
    private InspectionEnums.EFoodStorage foodStorage;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_nearby")
    private InspectionEnums.ESourceNearby sourceNearby;

    @Column(name = "harvest_time")
    private boolean harvestTime;

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

    @Column(name = "none_brood")
    private boolean noneBrood;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stressors_id")
    private StressorsEntity stressors;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "symptoms_id")
    private SymptomsEntity symptoms;

    @Enumerated(EnumType.STRING)
    @Column(name = "colony_temperament")
    private InspectionEnums.EColonyTemperament temperament;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "notes")
    private String notes;
}

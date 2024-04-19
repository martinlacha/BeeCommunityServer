package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity of sensors data from monitoring
 */
@Entity
@Table(name = "SENSORS_DATA", schema = "public")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorsDataEntity {

    /**
     * Unique identification of hive entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hive_id")
    private HiveEntity hive;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "weight")
    private double weight;

    @Column(name = "hive_temperature")
    private double hiveTemperature;

    @Column(name = "hive_humidity")
    private double hiveHumidity;

    @Column(name = "outside_temperature")
    private double outsideTemperature;

    @Column(name = "outside_humidity")
    private double outsideHumidity;
}

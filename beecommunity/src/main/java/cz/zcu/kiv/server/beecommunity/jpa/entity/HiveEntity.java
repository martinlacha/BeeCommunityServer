package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.HiveEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "apiary_id")
    private ApiaryEntity apiary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "queen_id")
    private QueenEntity queen;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private HiveEnums.EColor color;

    @Enumerated(EnumType.STRING)
    @Column(name = "bee_source")
    private HiveEnums.EBeeSource source;

    @Column(name = "date_establishment")
    private LocalDate establishment;

    @Column(name = "structure")
    private String structure;

    @Column(name = "notes")
    private String notes;

    @Column(name = "image", length = 1024)
    private byte[] image;

    @Override
    public String toString() {
        return "HiveEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", source=" + source +
                '}';
    }
}

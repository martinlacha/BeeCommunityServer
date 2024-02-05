package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.ApiaryEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "APIARY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiaryEntity {

    /**
     * Unique identification of apiary entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment")
    private ApiaryEnums.EEnvironment environment;

    @Enumerated(EnumType.STRING)
    @Column(name = "terrain")
    private ApiaryEnums.ETerrain terrain;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "apiary", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<HiveEntity> hives;
}

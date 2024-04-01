package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.ApiaryEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of apiary
 */
@Entity
@Table(name = "APIARY")
@Data
@Builder
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

    @Column(name = "name")
    private String name;

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

    @Column(name = "image", length = 1024)
    private byte[] image;
}

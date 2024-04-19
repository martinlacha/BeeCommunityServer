package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of user address
 */
@Entity
@Table(name = "ADDRESS", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {

    /**
     * Unique identification of address entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "town")
    private String town;

    @Column(name = "street")
    private String street;

    @Column(name = "house_number")
    private int number;
}

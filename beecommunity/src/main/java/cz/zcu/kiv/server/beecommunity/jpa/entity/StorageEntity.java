package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of storage
 */
@Entity
@Table(name = "STORAGE", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageEntity {

    /**
     * Unique identification of storage entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item")
    private String item;

    @Column(name = "count")
    private int count;
}

package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity of role
 */
@Entity
@Table(name = "AUTH_ROLE", schema = "public")
@Data
public class RoleEntity {
    /**
     * Id of role record in table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name
     */
    @Column(name = "role_name")
    private String role;
}

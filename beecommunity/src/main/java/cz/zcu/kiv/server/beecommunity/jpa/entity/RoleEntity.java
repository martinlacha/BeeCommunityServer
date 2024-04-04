package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of role
 */
@Entity
@Table(name = "AUTH_ROLE", schema = "public")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    /**
     * ID of role record in table
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

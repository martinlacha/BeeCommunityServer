package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity of personal user information
 */
@Getter
@Entity
@Table(name = "USER_INFO", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoEntity {

    /**
     * Unique identification of user entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "created_date", nullable = false)
    private LocalDate created;

    @Column(name = "experience", nullable = false)
    private UserEnums.EExperience experience;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @Override
    public String toString() {
        return "UserInfoEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}

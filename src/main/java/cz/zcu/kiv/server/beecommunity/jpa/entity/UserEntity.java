package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Entity of user in database
 */
@Getter
@Entity
@Table(name = "AUTH_USER", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    /**
     * Unique identification of user entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Email can't be empty.")
    @Email(message = "Not valid email address.")
    @Column(name = "email", unique = true)
    private String email;

    @NotEmpty(message = "Name can't be empty.")
    @NotBlank(message = "Name can't be blank.")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "login_attempts")
    private int loginAttempts;

    @Column(name = "suspended")
    private boolean suspended;

    @Column(name = "new_account")
    private boolean newAccount;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "AUTH_USER_ROLE", joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_info_id")
    private UserInfoEntity userInfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        roles.forEach(role -> authorityList.add(new SimpleGrantedAuthority(role.getRole())));
        return authorityList;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return loginAttempts < MAX_LOGIN_ATTEMPTS;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !suspended;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * @return user first name and surname as String if it is not null, otherwise user id
     */
    public String getFullName() {
        return getUserInfo() != null ?
                String.format("%s %s", userInfo.getName(), userInfo.getSurname()) :
                String.format("User Id: %d", id);
    }

    /**
     * Check if user has admin role
     * @return true if user has admin role, otherwise false
     */
    public boolean hasRole(UserEnums.ERoles role) {
        return roles.stream().anyMatch(roleEntity -> roleEntity.getRole().contains(role.name()));
    }
}
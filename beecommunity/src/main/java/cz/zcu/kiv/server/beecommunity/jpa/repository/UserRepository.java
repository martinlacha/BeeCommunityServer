package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(@NotNull String email);

    Optional<UserEntity> findByEmail(@NotNull String email);

    // Find users that contain email, name or surname text (ignore case)
    List<UserEntity> findByEmailContainsIgnoreCaseOrUserInfoNameContainsIgnoreCaseOrUserInfoSurnameContainsIgnoreCase
            (@NotNull String email,
             @NotNull String name,
             @NotNull String surname);

    // Count user is non/activated account
    int countByNewAccount(boolean isNewAccount);

    @Query("SELECT r.userInfo.created, COUNT(r) FROM UserEntity r WHERE r.userInfo IS NOT NULL GROUP BY r.userInfo.created ORDER BY r.userInfo.created")
    List<Object[]> findCountUsersByCreatedDate();
}

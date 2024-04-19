package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
}

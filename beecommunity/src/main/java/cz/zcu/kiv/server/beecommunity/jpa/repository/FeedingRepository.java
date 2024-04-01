package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveFeedingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedingRepository extends JpaRepository<HiveFeedingEntity, Long> {
}

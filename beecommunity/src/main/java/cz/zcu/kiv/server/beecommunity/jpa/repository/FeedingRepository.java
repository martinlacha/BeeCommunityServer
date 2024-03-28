package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveFeedingEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.InspectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedingRepository extends JpaRepository<HiveFeedingEntity, Long> {
}

package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.InspectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InspectionRepository extends JpaRepository<InspectionEntity, Long> {
    List<InspectionEntity> findByOwnerIdAndHiveIdOrderByInspectionDate(Long userId, Long hiveId);
    Optional<InspectionEntity> findByOwnerIdAndId(Long userId, Long hiveId);
}

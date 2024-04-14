package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.SensorsDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorsDataRepository extends JpaRepository<SensorsDataEntity, Long> {
    List<SensorsDataEntity> findByHiveIdOrderByTime(Long hiveId);
}

package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HiveRepository extends JpaRepository<HiveEntity, Long> {
    List<HiveEntity> findByOwnerIdAndApiaryIdOrderById(Long userId, Long apiaryId);
}
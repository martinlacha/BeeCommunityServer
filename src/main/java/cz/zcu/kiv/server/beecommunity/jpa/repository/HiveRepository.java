package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.enums.HiveEnums;
import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HiveRepository extends JpaRepository<HiveEntity, Long> {
    List<HiveEntity> findByApiaryIdOrderById(Long apiaryId);

    List<HiveEntity> findByOwnerId(Long userId);

    @Query("SELECT COUNT(h.owner.id) FROM HiveEntity h WHERE YEAR(h.establishment) = :year AND h.owner.id = :userId GROUP BY h.owner.id")
    Object countByOwnerIdAndEstablishmentYear(Long userId, Integer year);

    int countByOwnerIdAndSource(Long userId, HiveEnums.EBeeSource source);

    int countByOwnerIdAndApiaryIdAndSource(Long userId, Long apiaryId, HiveEnums.EBeeSource source);

    List<HiveEntity> findByOwnerIdAndApiaryIdOrderByEstablishmentAsc(Long userId, Long apiaryId);

    List<HiveEntity> findByOwnerIdAndApiaryIdOrderByEstablishmentDesc(Long userId, Long apiaryId);

    @Query("SELECT COUNT(h), h.owner.email FROM HiveEntity h WHERE h.owner.userInfo IS NOT NULL GROUP BY h.owner ORDER BY COUNT(h) DESC")
    List<Object[]> countHivesGroupByOwner();

    @Query("SELECT r.establishment, COUNT(r) FROM HiveEntity r GROUP BY r.establishment ORDER BY r.establishment")
    List<Object[]> findCountHivesByEstablishment();
}
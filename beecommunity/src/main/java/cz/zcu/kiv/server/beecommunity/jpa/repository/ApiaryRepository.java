package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.ApiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiaryRepository extends JpaRepository<ApiaryEntity, Long> {
    // Find apiary by owner id and order them by id
    List<ApiaryEntity> findByOwnerIdOrderById(Long userId);

    // Count apiaries for every user
    @Query("SELECT COUNT(r), r.owner.email FROM ApiaryEntity r WHERE r.owner.userInfo IS NOT NULL GROUP BY r.owner ORDER BY COUNT(r) DESC")
    List<Object[]> countApiariesGroupByOwner();


    @Query("SELECT COUNT(r), r.environment FROM ApiaryEntity r GROUP BY r.environment ORDER BY COUNT(r) DESC")
    List<Object[]> countApiariesGroupByEnvironment();

    @Query("SELECT COUNT(r), r.terrain FROM ApiaryEntity r GROUP BY r.terrain ORDER BY COUNT(r) DESC")
    List<Object[]> countApiariesGroupByTerrain();
}

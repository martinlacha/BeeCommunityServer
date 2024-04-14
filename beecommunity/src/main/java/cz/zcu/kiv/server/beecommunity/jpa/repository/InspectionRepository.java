package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import cz.zcu.kiv.server.beecommunity.jpa.entity.InspectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InspectionRepository extends JpaRepository<InspectionEntity, Long> {
    List<InspectionEntity> findByHiveIdOrderById(Long hiveId);

    List<InspectionEntity> findByHiveIdOrderByInspectionDate(Long hiveId);

    Optional<InspectionEntity> findByOwnerIdAndId(Long userId, Long hiveId);

    @Query("SELECT SUM(r.harvest.productQuantity) " +
            "FROM InspectionEntity r " +
            "WHERE year(r.inspectionDate) = :year " +
            "AND r.harvest.product = :product " +
            "AND r.harvest.productUnit = :unit")
    Double sumQuantityByProductAndUnitTypeAndYear
            (@Param("year") int year,
             @Param("product") InspectionEnums.EHarvestProduct product,
             @Param("unit") InspectionEnums.EUnitsAndDoses unit);

    @Query("SELECT r.treatment.disease FROM InspectionEntity r WHERE r.treatment.disease != 'UNSPECIFIED' GROUP BY r.treatment.disease ORDER BY COUNT(r) DESC LIMIT 1")
    List<Object[]> getMostCommonDisease();

    @Query("SELECT r.feeding.food FROM InspectionEntity r WHERE r.feeding.food != 'UNSPECIFIED' GROUP BY r.feeding.food ORDER BY COUNT(r) DESC LIMIT 1")
    List<Object[]> getMostCommonFoodType();

    @Query("SELECT r.treatment.disease FROM InspectionEntity r WHERE r.treatment.disease != 'UNSPECIFIED' AND r.hive.id = :hiveId GROUP BY r.treatment.disease ORDER BY COUNT(r) DESC LIMIT 1")
    List<Object[]> getMostCommonDiseaseInHive(@Param("hiveId") Long hiveId);

}

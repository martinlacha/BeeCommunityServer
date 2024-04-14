package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.SensorDataDto;
import cz.zcu.kiv.server.beecommunity.services.IHiveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for apiaries endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/hive")
@Tag(name = "Hive")
@AllArgsConstructor
public class HiveController {
    private final IHiveService hiveService;

    /**
     * Return list of hives
     * @return list of hives
     */
    @GetMapping
    ResponseEntity<List<HiveDto>> getHives(@RequestParam Long apiaryId) {
        return hiveService.getHives(apiaryId);
    }

    /**
     * Create new hive for user
     * @param hiveDto dto with information and image
     * @return status code of operation result
     */
    @PostMapping
    ResponseEntity<Void> createHive(@ModelAttribute @Valid HiveDto hiveDto) {
        return hiveService.createHive(hiveDto);
    }

    /**
     * Update hive by id
     * @param hiveDto dto with information and image
     * @return status code of operation result
     */
    @PutMapping
    ResponseEntity<Void> updateHiveById(@ModelAttribute @Valid HiveDto hiveDto) {
        return hiveService.updateHive(hiveDto);
    }

    /**
     * Delete hive by id
     * @param hiveId id of hive to delete
     * @return status code of operation result
     */
    @DeleteMapping
    ResponseEntity<Void> deleteHiveById(@RequestParam Long hiveId) {
        return hiveService.deleteHive(hiveId);
    }

    /**
     * Return detail of hive
     * @param hiveId id of hive
     * @return detail of hive
     */
    @GetMapping("/detail")
    ResponseEntity<HiveDto> getHiveDetail(@RequestParam Long hiveId) {
        return hiveService.getHiveDetail(hiveId);
    }

    /**
     * Get hive image if was uploaded
     * @param hiveId hive id
     * @return byte array of image
     */
    @GetMapping("/image")
    ResponseEntity<byte[]> getHiveImage(@RequestParam Long hiveId) {
        return hiveService.getHiveImage(hiveId);
    }

    /**
     * Find and return string representation of hive structure
     * @param hiveId hive id
     * @return String representation of structure
     */
    @GetMapping("/structure")
    ResponseEntity<String> getHiveStructure(@RequestParam Long hiveId) {
        return hiveService.getHiveStructure(hiveId);
    }

    /**
     * Create or update hive structure
     * @param hiveId hive id
     * @param structure string representation of hive structure
     * @return status code of operation result
     */
    @PostMapping("/structure")
    ResponseEntity<Void> createHiveStructure(@RequestParam Long hiveId, @RequestParam String structure) {
        return hiveService.createHiveStructure(hiveId, structure);
    }

    /**
     * Create new record with data from monitoring hive
     * @param hiveId id of hive
     * @param data measured data from sensors
     * @return status co of operation result
     */
    @PostMapping("/sensors")
    ResponseEntity<Void> uploadSensorsData(@RequestParam Long hiveId, @RequestParam SensorDataDto data) {
        return hiveService.uploadSensorsData(hiveId, data);
    }

    /**
     * Get list of sensors data from single hive
     * @param hiveId id of hive
     * @return list of monitoring data from hive sensors
     */
    @GetMapping("/sensors")
    ResponseEntity<List<SensorDataDto>> getSensorsData(@RequestParam Long hiveId) {
        return hiveService.getHiveSensorsData(hiveId);
    }
}

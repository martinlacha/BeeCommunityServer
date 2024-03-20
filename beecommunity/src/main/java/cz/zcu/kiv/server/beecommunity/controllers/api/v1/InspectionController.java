package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDto;
import cz.zcu.kiv.server.beecommunity.services.IInspectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for inspection endpoints
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/inspection")
@Tag(name = "Inspection")
@AllArgsConstructor
public class InspectionController {
    private final IInspectionService inspectionService;

    /**
     * Return list of inspections of hive
     * @return list of hive inspections
     */
    @GetMapping
    ResponseEntity<List<InspectionDto>> getInspections(@RequestParam Long hiveId) {
        return inspectionService.getInspections(hiveId);
    }

    /**
     * Create new inspection for hive
     * @param inspectionDto dto with information about inspection
     * @return status of operation result
     */
    @PostMapping
    ResponseEntity<Void> createInspection(@RequestBody InspectionDetailDto inspectionDto) {
        return inspectionService.createInspection(inspectionDto);
    }

    /**
     * Update existing inspection
     * @param inspectionDto inspection dto
     * @return status of operation result
     */
    @PutMapping
    ResponseEntity<Void> updateInspection(@RequestBody InspectionDetailDto inspectionDto) {
        return inspectionService.updateInspection(inspectionDto);
    }

    /**
     * Delete inspection by id
     * @param inspectionId inspection id
     * @return status of operation result
     */
    @DeleteMapping
    ResponseEntity<Void> deleteInspection(@RequestParam Long inspectionId) {
        return inspectionService.deleteInspection(inspectionId);
    }

    /**
     * Return detail information about inspection, feeding, treatment and harvest
     * @param inspectionId inspection id
     * @return dto with inspection detail information
     */
    @GetMapping("/detail")
    ResponseEntity<InspectionDetailDto> getInspectionDetail(@RequestParam Long inspectionId) {
        return inspectionService.getInspectionDetail(inspectionId);
    }

    /**
     * Return byte array of image find by hiveId and image type
     * @param inspectionId id of hive
     * @param imageType type of image from hive inspection
     * @return byte array of decompressed image from inspection
     */
    @GetMapping("/image")
    ResponseEntity<byte[]> getImageByType(@RequestParam Long inspectionId, @RequestParam InspectionEnums.EImageType imageType) {
        return inspectionService.getImageByType(inspectionId, imageType);
    }
}

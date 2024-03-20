package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDto;
import cz.zcu.kiv.server.beecommunity.jpa.repository.InspectionRepository;
import cz.zcu.kiv.server.beecommunity.services.IInspectionService;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InspectionServiceImpl implements IInspectionService {
    private final InspectionRepository inspectionRepository;

    private final ObjectMapper modelMapper;

    /**
     * Find all inspections of hive
     * @return list of inspections of specific hive
     */
    @Override
    public ResponseEntity<List<InspectionDto>> getInspections(Long hiveId) {
        var user = UserUtils.getUserFromSecurityContext();
        var entitiesList = inspectionRepository.findByOwnerIdAndHiveIdOrderById(user.getId(), hiveId);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertInspectionEntityList(entitiesList));
    }

    /**
     * Create new inspection for hive
     * @param inspectionDto dto with information about inspection
     * @return status of operation result
     */
    @Override
    public ResponseEntity<Void> createInspection(InspectionDetailDto inspectionDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var inspectionEntity = modelMapper.convertInspectionDto(inspectionDto);
        inspectionEntity.setOwner(user);
        inspectionRepository.saveAndFlush(inspectionEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Update existing inspection
     * @param inspectionDto inspection dto
     * @return status of operation result
     */
    @Override
    public ResponseEntity<Void> updateInspection(InspectionDetailDto inspectionDto) {
        return null;
    }

    /**
     * Delete inspection by id
     * @param inspectionId inspection id
     * @return status of operation result
     */
    @Override
    public ResponseEntity<Void> deleteInspection(Long inspectionId) {
        var user = UserUtils.getUserFromSecurityContext();
        var inspection = inspectionRepository.findByOwnerIdAndId(user.getId(), inspectionId);
        if (inspection.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        inspectionRepository.delete(inspection.get());
        inspectionRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Return detail information about inspection, feeding, treatment and harvest
     * @param inspectionId inspection id
     * @return dto with inspection detail information
     */
    @Override
    public ResponseEntity<InspectionDetailDto> getInspectionDetail(Long inspectionId) {
        var user = UserUtils.getUserFromSecurityContext();
        var inspection = inspectionRepository.findByOwnerIdAndId(user.getId(), inspectionId);
        return inspection
                .map(inspectionEntity -> ResponseEntity.status(HttpStatus.OK)
                        .body(modelMapper.convertInspectionEntity(inspectionEntity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Return byte array of image find by hiveId and image type
     * @param inspectionId id of inspection
     * @param imageType type of image from hive inspection
     * @return byte array of decompressed image from inspection
     */
    @Override
    public ResponseEntity<byte[]> getImageByType(Long inspectionId, InspectionEnums.EImageType imageType) {
        byte[] image = new byte[0];
        var user = UserUtils.getUserFromSecurityContext();
        var inspection = inspectionRepository.findByOwnerIdAndId(user.getId(), inspectionId);
        if (inspection.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        switch (imageType) {
            case INSPECTION -> image = inspection.get().getInspectionImage();
            case FOOD -> image = inspection.get().getFoodImage();
            case POPULATION -> image = inspection.get().getPopulationImage();
            case QUEEN -> image = inspection.get().getQueenImage();
            case BROOD -> image = inspection.get().getBroodImage();
            case STRESSORS -> image = inspection.get().getStressorsImage();
            case DISEASE -> image = inspection.get().getDiseaseImage();
        }
        if (image.length != 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ImageUtil.decompressImage(inspection.get().getInspectionImage()));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
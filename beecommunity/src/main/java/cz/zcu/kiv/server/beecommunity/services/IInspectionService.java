package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IInspectionService {
    ResponseEntity<List<InspectionDto>> getInspections(Long hiveId);
    ResponseEntity<Void> createInspection(InspectionDetailDto inspectionDto);
    ResponseEntity<Void> updateInspection(InspectionDetailDto inspectionDto);
    ResponseEntity<Void> deleteInspection(Long inspectionId);
    ResponseEntity<InspectionDetailDto> getInspectionDetail(Long inspectionId);
    ResponseEntity<byte[]> getImageByType(Long inspectionId, InspectionEnums.EImageType imageType);
}

package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for treatment
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentDto {

    private InspectionEnums.EDisease disease;

    private String treatment;

    private int quantity;

    private InspectionEnums.EUnitsAndDoses dose;

    private String startDate;

    private String endDate;
}

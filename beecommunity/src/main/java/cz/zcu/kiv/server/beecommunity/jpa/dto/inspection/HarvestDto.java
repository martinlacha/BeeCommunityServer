package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for harvest
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HarvestDto {

    private InspectionEnums.EHarvestProduct product;

    private int productQuantity;

    private InspectionEnums.EUnitsAndDoses productUnit;

    private int frameCount;

    private int superCount;
}

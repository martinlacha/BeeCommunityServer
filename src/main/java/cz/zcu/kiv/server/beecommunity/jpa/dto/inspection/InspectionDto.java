package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for inspection
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectionDto {
    private Long id;

    private Long hiveId;

    private InspectionEnums.EType type;

    private String date;

    private InspectionEnums.EPopulation population;

    private InspectionEnums.EFoodStorage food;

    private InspectionEnums.ESourceNearby sourceNearby;

    private boolean hasQueen;

    private boolean hasBrood;

    private boolean hasDisease;
}

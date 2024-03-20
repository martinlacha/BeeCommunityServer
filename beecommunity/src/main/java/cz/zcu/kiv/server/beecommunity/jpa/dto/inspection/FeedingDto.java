package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for hive feeding
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedingDto {

    private InspectionEnums.EFoodType food;

    private InspectionEnums.EFoodRatio ratio;

    private int foodQuantity;

    private InspectionEnums.EUnitsAndDoses foodUnit;
}

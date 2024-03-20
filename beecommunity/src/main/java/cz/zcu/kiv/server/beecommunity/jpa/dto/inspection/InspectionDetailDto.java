package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dto for inspection detail
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectionDetailDto {
    private Long id;

    @NotNull(message = "Hive id can't be null")
    private Long hiveId;

    @NotNull(message = "Type can't be null")
    private InspectionEnums.EType type;

    @NotNull(message = "Date can't be null")
    private String date;

    private InspectionEnums.EWeather weather;

    @NotNull(message = "Population can't be null")
    private InspectionEnums.EPopulation population;

    @NotNull(message = "Food storage can't be null")
    private InspectionEnums.EFoodStorage foodStorage;

    @NotNull(message = "Sources nearby can't be null")
    private InspectionEnums.ESourceNearby sourceNearby;

    @NotNull(message = "Brood pattern can't be null")
    private InspectionEnums.EBroodPattern broodPattern;
    private boolean hasQueen;
    private boolean hasEggs;
    private boolean hasUncappedBrood;
    private boolean hasCappedBrood;
    private InspectionEnums.EColonyTemperament colonyTemperament;

    // Stressors
    private StressorsDto stressors;

    // Treatment
    private TreatmentDto treatment;

    // Feeding
    private FeedingDto feeding;

    // Harvest
    private HarvestDto harvest;

    // Notes
    private String notes;

    // Images
    private MultipartFile inspectionImage;
    private MultipartFile foodImage;
    private MultipartFile populationImage;
    private MultipartFile queenImage;
    private MultipartFile broodImage;
    private MultipartFile stressorsImage;
    private MultipartFile diseaseImage;
}

package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

/**
 * Dto for hive feeding
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedingDto {

    FeedingDto(String json) {
        this.json = json;
    }


    private InspectionEnums.EFoodType food;

    private InspectionEnums.EFoodRatio ratio;

    private double foodQuantity;

    private InspectionEnums.EUnitsAndDoses foodUnit;

    // Json string to deserialize dto
    private String json;

    // Deserialize String json value to dto object
    public FeedingDto deserializeJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, this.getClass());
        } catch (JsonMappingException e) {
            log.error("Error while mapping feeding dto: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("Error while processing feeding dto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

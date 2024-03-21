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
 * Dto for harvest
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HarvestDto {
    HarvestDto(String json) {
        this.json = json;
    }

    private InspectionEnums.EHarvestProduct product;

    private double productQuantity;

    private InspectionEnums.EUnitsAndDoses productUnit;

    private int frameCount;

    private int superCount;

    // Json string to deserialize
    private String json;

    // Deserialize String json value to dto object
    public HarvestDto deserializeJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, this.getClass());
        } catch (JsonMappingException e) {
            log.error("Error while mapping harvest dto: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("Error while processing harvest dto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

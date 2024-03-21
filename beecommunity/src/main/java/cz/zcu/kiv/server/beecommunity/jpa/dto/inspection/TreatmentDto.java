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
 * Dto for treatment
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentDto {
    TreatmentDto(String json) {
        this.json = json;
    }

    private InspectionEnums.EDisease disease;

    private String treatment;

    private double quantity;

    private InspectionEnums.EUnitsAndDoses dose;

    private String startDate;

    private String endDate;

    // Json string to deserialize dto
    private String json;

    // Deserialize String json value to dto object
    public TreatmentDto deserializeJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, this.getClass());
        } catch (JsonMappingException e) {
            log.error("Error while mapping treatment dto: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("Error while processing treatment dto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

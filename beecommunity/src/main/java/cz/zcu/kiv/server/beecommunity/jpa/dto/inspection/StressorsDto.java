package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

/**
 * Dto for bee stressors
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StressorsDto {
    StressorsDto(String json) {
        this.json = json;
    }

    private boolean varroaMites;

    private boolean chalkbrood;

    private boolean sacbrood;

    private boolean foulbrood;

    private boolean nosema;

    private boolean beetles;

    private boolean mice;

    private boolean ants;

    private boolean moths;

    private boolean wasps;

    private boolean hornet;

    // Json string to deserialize dto
    private String json;

    // Deserialize String json value to dto object
    public StressorsDto deserializeJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, this.getClass());
        } catch (JsonMappingException e) {
            log.error("Error while mapping stressors dto: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("Error while processing stressors dto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

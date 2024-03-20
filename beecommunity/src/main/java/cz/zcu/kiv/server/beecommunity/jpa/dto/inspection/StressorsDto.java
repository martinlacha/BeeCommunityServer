package cz.zcu.kiv.server.beecommunity.jpa.dto.inspection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for bee stressors
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StressorsDto {

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
}

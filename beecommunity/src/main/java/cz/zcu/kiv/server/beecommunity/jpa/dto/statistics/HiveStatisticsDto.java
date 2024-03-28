package cz.zcu.kiv.server.beecommunity.jpa.dto.statistics;

import cz.zcu.kiv.server.beecommunity.enums.HiveEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Statistics for specific hive
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HiveStatisticsDto {
    private Long id;
    private String name;
    private HiveEnums.EBeeSource beeSource;
    private List<InspectionDetailDto> inspections;
}

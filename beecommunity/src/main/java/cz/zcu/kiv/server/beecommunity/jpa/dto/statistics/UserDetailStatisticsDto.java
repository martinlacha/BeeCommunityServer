package cz.zcu.kiv.server.beecommunity.jpa.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dto with detail personal user statistics
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailStatisticsDto {

    // Apiary
    private int countOfApiaries;

    // Hive
    private int countOfHivesLastYear;
    private int countOfHivesCurrentYear;
    private int countSwarmHives;
    private int countNucHives;
    private int countPackageHives;
    private int countSplitHives;
    private int countAcquiredHives;
    private int countOtherHives;

    private List<ApiaryStatisticsDto> apiariesStatistics;
}

package cz.zcu.kiv.server.beecommunity.jpa.dto.statistics;

import cz.zcu.kiv.server.beecommunity.enums.ApiaryEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Statistics for specific apiary
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiaryStatisticsDto {
    private Long id;
    private String name;
    private ApiaryEnums.EEnvironment environment;
    private ApiaryEnums.ETerrain terrain;

    // Apiary statistics
    private Long countOfHivesLastYear;
    private Long countOfHivesCurrentYear;
    private int countSwarmHives;
    private int countNucHives;
    private int countPackageHives;
    private int countSplitHives;
    private int countAcquiredHives;
    private int countOtherHives;

    private String oldestHive;
    private String youngestHive;

    // Honey
    private String mostHoneyHiveCurrentYear;
    private double mostHoneyCurrentYear;
    private String mostHoneyHiveLastYear;
    private double mostHoneyLastYear;
    private double totalHoneyCurrentYear;
    private double totalHoneyLastYear;
    // WAX
    private String mostWaxHiveCurrentYear;
    private double mostWaxCurrentYear;
    private String mostWaxHiveLastYear;
    private double mostWaxLastYear;
    private double totalWaxCurrentYear;
    private double totalWaxLastYear;
    // PROPOLIS
    private String mostPropolisHiveCurrentYear;
    private double mostPropolisCurrentYear;
    private String mostPropolisHiveLastYear;
    private double mostPropolisLastYear;
    private double totalPropolisCurrentYear;
    private double totalPropolisLastYear;
    // POLLEN
    private String mostPollenHiveCurrentYear;
    private double mostPollenCurrentYear;
    private String mostPollenHiveLastYear;
    private double mostPollenLastYear;
    private double totalPollenCurrentYear;
    private double totalPollenLastYear;
    // ROYAL_JELLY
    private String mostJellyHiveCurrentYear;
    private double mostJellyCurrentYear;
    private String mostJellyHiveLastYear;
    private double mostJellyLastYear;
    private double totalJellyCurrentYear;
    private double totalJellyLastYear;

    private List<HiveStatisticsDto> hivesStatistics;
}

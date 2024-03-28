package cz.zcu.kiv.server.beecommunity.jpa.dto.statistics;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dto for general statistics
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralStatisticsDto {
    // Users
    private int countOfUsers;
    private int activatedUsers;
    private List<GraphOverviewItem> userCountOverviewMap;

    // Apiary
    private int countOfApiaries;
    private Object maxApiaries;
    private Object minApiaries;

    // Hives
    private int countOfHives;
    private Object maxHivesInApiary;
    private Object minHivesInApiary;
    private List<GraphOverviewItem> hiveCountOverviewMap;

    // Community
    private int countPosts;
    private int countPublicPosts;
    private double averageDailyPosts;
    private Object topPostUser;
    private List<GraphOverviewItem> postsCountOverviewMap;

    // News
    private int countNews;
    private double averageDailyNews;
    private Object topNewsUser;
    private List<GraphOverviewItem> newsCountOverviewMap;

    // Honey
    private double totalHoneyCurrentYear;
    private double totalHoneyLastYear;
    // Wax
    private double totalWaxCurrentYear;
    private double totalWaxLastYear;
    // Propolis
    private double totalPropolisCurrentYear;
    private double totalPropolisLastYear;
    // Pollen
    private double totalPollenCurrentYear;
    private double totalPollenLastYear;
    // Jelly
    private double totalJellyCurrentYear;
    private double totalJellyLastYear;

    // Disease
    private InspectionEnums.EDisease mostCommonDisease;

    // Feeding
    private InspectionEnums.EFoodType mostCommonFood;

}

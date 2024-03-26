package cz.zcu.kiv.server.beecommunity.jpa.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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

    // Inspections
}

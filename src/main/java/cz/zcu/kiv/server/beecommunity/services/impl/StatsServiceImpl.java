package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.*;
import cz.zcu.kiv.server.beecommunity.jpa.entity.ApiaryEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.InspectionEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.*;
import cz.zcu.kiv.server.beecommunity.services.IFriendService;
import cz.zcu.kiv.server.beecommunity.services.IStatsService;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static cz.zcu.kiv.server.beecommunity.enums.HiveEnums.EBeeSource;
import static cz.zcu.kiv.server.beecommunity.enums.InspectionEnums.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StatsServiceImpl implements IStatsService {
    private final UserRepository userRepository;
    private final ApiaryRepository apiaryRepository;
    private final HiveRepository hiveRepository;
    private final NewsRepository newsRepository;
    private final CommunityPostRepository communityRepository;
    private final InspectionRepository inspectionRepository;
    private final IFriendService friendService;
    private final FriendshipUtils friendshipUtils;
    private final ObjectMapper modelMapper;

    /**
     * General statistics from all users in system
     * @return general statistics
     */
    @Override
    public ResponseEntity<GeneralStatisticsDto> getGeneralStatistics() {
        GeneralStatisticsDto stats = new GeneralStatisticsDto();

        // Users
        stats.setCountOfUsers((int) userRepository.count());
        stats.setActivatedUsers(userRepository.countByNewAccount(false));
        stats.setUserCountOverviewMap(modelMapper.convertObjectTimelineCounts(userRepository.findCountUsersByCreatedDate()));

        // Apiary
        stats.setCountOfApiaries((int) apiaryRepository.count());
        // Take only users there are activated (fill their personal information)
        var countApiariesByUsers = apiaryRepository.countApiariesGroupByOwner();
        if (!countApiariesByUsers.isEmpty()) {
            stats.setMaxApiaries(countApiariesByUsers.get(0));
            stats.setMinApiaries(countApiariesByUsers.get(countApiariesByUsers.size() - 1));
        }

        //Hive
        stats.setCountOfHives((int) hiveRepository.count());
        stats.setHiveCountOverviewMap(modelMapper.convertObjectTimelineCounts(hiveRepository.findCountHivesByEstablishment()));
        var countHivesByUsers = hiveRepository.countHivesGroupByOwner();
        if (!countHivesByUsers.isEmpty()) {
            stats.setMaxHivesInApiary(countHivesByUsers.get(0));
            stats.setMinHivesInApiary(countHivesByUsers.get(countHivesByUsers.size() - 1));
        }

        // News
        stats.setCountNews((int) newsRepository.count());
        stats.setTopNewsUser(newsRepository.findTopUserByNewsCount());
        stats.setNewsCountOverviewMap(modelMapper.convertObjectTimelineCounts(newsRepository.findCountNewsByCreatedDate()));
        var dailyCountsNews = newsRepository.countsDailyNews();
        if (!dailyCountsNews.isEmpty()) {
            stats.setAverageDailyNews((double) dailyCountsNews.stream().mapToInt(Integer::intValue).sum()/dailyCountsNews.size());
        }

        // Community
        stats.setCountPosts((int) communityRepository.count());
        stats.setCountPublicPosts(communityRepository.countByAccess(CommunityEnums.EAccess.PUBLIC));
        stats.setTopPostUser(communityRepository.findTopUserByPostCount());
        stats.setPostsCountOverviewMap(modelMapper.convertObjectTimelineCounts(communityRepository.findCountPostsByCreatedDate()));
        var dailyCountsPosts = communityRepository.countsDailyPosts();
        if (!dailyCountsPosts.isEmpty()) {
            stats.setAverageDailyPosts((double) dailyCountsPosts.stream().mapToInt(Integer::intValue).sum()/dailyCountsPosts.size());
        }

        var currentYear = LocalDate.now().getYear();
        var lastYear = LocalDate.now().getYear() - 1;

        stats.setTotalHoneyCurrentYear(calculateHoneyTotal(currentYear));
        stats.setTotalHoneyLastYear(calculateHoneyTotal(lastYear));

        stats.setTotalWaxCurrentYear(calculateTotalProduct(currentYear, EHarvestProduct.WAX));
        stats.setTotalWaxLastYear(calculateTotalProduct(lastYear, EHarvestProduct.WAX));

        stats.setTotalPropolisCurrentYear(calculateTotalProduct(currentYear, EHarvestProduct.PROPOLIS));
        stats.setTotalPropolisLastYear(calculateTotalProduct(lastYear, EHarvestProduct.PROPOLIS));

        stats.setTotalPollenCurrentYear(calculateTotalProduct(currentYear, EHarvestProduct.POLLEN));
        stats.setTotalPollenLastYear(calculateTotalProduct(lastYear, EHarvestProduct.POLLEN));

        stats.setTotalJellyCurrentYear(calculateTotalProduct(currentYear, EHarvestProduct.ROYAL_JELLY));
        stats.setTotalJellyLastYear(calculateTotalProduct(lastYear, EHarvestProduct.ROYAL_JELLY));

        var mostCommonDisease = inspectionRepository.getMostCommonDisease();
        if (!mostCommonDisease.isEmpty()) {
            EDisease disease = (EDisease) mostCommonDisease.get(0)[0];
            stats.setMostCommonDisease(disease);
        }
        var mostCommonFoodType = inspectionRepository.getMostCommonFoodType();
        if (!mostCommonFoodType.isEmpty()) {
            EFoodType foodType = (EFoodType) mostCommonFoodType.get(0)[0];
            stats.setMostCommonFood(foodType);
        }

        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }

    /**
     * Return friends statistics
     * @return friends statistics
     */
    @Override
    public ResponseEntity<FriendsStatisticsDto> getFriendsStatistics() {
        FriendsStatisticsDto friendsStatisticsDto = FriendsStatisticsDto
                .builder()
                .friends(friendService.getMyFriends().getBody())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(friendsStatisticsDto);
    }

    /**
     * Return personal statistics
     * @return detailed personal statistics
     */
    @Override
    public ResponseEntity<UserDetailStatisticsDto> getPersonalStatistics() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getUserDetailStatistics(UserUtils.getUserFromSecurityContext().getId()));
    }

    /**
     * Return detailed friend statistics
     * @return detailed statistics of friend
     */
    @Override
    public ResponseEntity<UserDetailStatisticsDto> getFriendDetailStatistics(String email) {
        var user = UserUtils.getUserFromSecurityContext();
        var friend = userRepository.findByEmail(email);
        if (friend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!friendshipUtils.isFriendshipStatus(user.getId(), friend.get().getId(), FriendshipEnums.EStatus.FRIEND)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(getUserDetailStatistics(friend.get().getId()));
    }

    /**
     * Get user detailed statistics by user id
     * @param userId user id
     * @return detailed statistics
     */
    private UserDetailStatisticsDto getUserDetailStatistics(Long userId) {
        var currentYear = LocalDate.now().getYear();
        var lastYear = LocalDate.now().getYear() - 1;
        return UserDetailStatisticsDto
                .builder()
                .countOfApiaries(apiaryRepository.countById(userId))
                .countOfHivesLastYear((long) hiveRepository.findByOwnerId(userId).stream().filter(hiveEntity -> hiveEntity.getEstablishment().getYear() <= lastYear).toList().size())
                .countOfHivesCurrentYear((long) hiveRepository.findByOwnerId(userId).stream().filter(hiveEntity -> hiveEntity.getEstablishment().getYear() <= currentYear).toList().size())
                .countSwarmHives(hiveRepository.countByOwnerIdAndSource(userId, EBeeSource.SWARM))
                .countNucHives(hiveRepository.countByOwnerIdAndSource(userId, EBeeSource.NUC))
                .countPackageHives(hiveRepository.countByOwnerIdAndSource(userId, EBeeSource.PACKAGE))
                .countSplitHives(hiveRepository.countByOwnerIdAndSource(userId, EBeeSource.SPLIT))
                .countAcquiredHives(hiveRepository.countByOwnerIdAndSource(userId, EBeeSource.ACQUIRED))
                .countOtherHives(hiveRepository.countByOwnerIdAndSource(userId, EBeeSource.OTHER))
                .apiariesStatistics(getApiariesStatistics(userId))
                .build();
    }

    /**
     * Get statistics for apiaries of user
     * @param userId user id
     * @return list of apiary statistics
     */
    private List<ApiaryStatisticsDto> getApiariesStatistics(Long userId) {
        var apiaries = apiaryRepository.findByOwnerIdOrderById(userId);
        return apiaries.stream().map(this::getApiaryStatistics).toList();
    }

    /**
     * Get statistics for single apiary
     * @param apiary to collect statistics from
     * @return calculated apiary statistics dto
     */
    private ApiaryStatisticsDto getApiaryStatistics(ApiaryEntity apiary) {
        var userId = apiary.getOwner().getId();
        // Current year (cy) and last year ly
        int cy = LocalDate.now().getYear();
        int ly = LocalDate.now().getYear() - 1;

        var hives = hiveRepository.findByApiaryIdOrderById(apiary.getId());

        var countOfHivesCY = hiveRepository.countByOwnerIdAndEstablishmentYear(userId, cy);
        var countOfHivesLY = hiveRepository.countByOwnerIdAndEstablishmentYear(userId, ly);
        var oldestHive = hiveRepository.findByOwnerIdAndApiaryIdOrderByEstablishmentAsc(userId, apiary.getId());
        var youngestHive = hiveRepository.findByOwnerIdAndApiaryIdOrderByEstablishmentDesc(userId, apiary.getId());

        var mostHoneyHiveCY = mostHoneyProductiveHive(hives, cy);
        var mostHoneyHiveLY = mostHoneyProductiveHive(hives, ly);
        var mostWaxHiveCY = mostProductiveHive(hives, cy, EHarvestProduct.WAX);
        var mostWaxHiveLY = mostProductiveHive(hives, ly, EHarvestProduct.WAX);
        var mostPropolisHiveCY = mostProductiveHive(hives, cy, EHarvestProduct.PROPOLIS);
        var mostPropolisHiveLY = mostProductiveHive(hives, ly, EHarvestProduct.PROPOLIS);
        var mostPollenHiveCY = mostProductiveHive(hives, cy, EHarvestProduct.POLLEN);
        var mostPollenHiveLY = mostProductiveHive(hives, ly, EHarvestProduct.POLLEN);
        var mostJellyHiveCY = mostProductiveHive(hives, cy, EHarvestProduct.ROYAL_JELLY);
        var mostJellyHiveLY = mostProductiveHive(hives, ly, EHarvestProduct.ROYAL_JELLY);


        return ApiaryStatisticsDto
                .builder()
                .id(apiary.getId())
                .name(apiary.getName())
                .environment(apiary.getEnvironment())
                .terrain(apiary.getTerrain())
                // Apiary
                .countOfHivesLastYear((Long)countOfHivesLY)
                .countOfHivesCurrentYear((Long)countOfHivesCY)
                .countSwarmHives(hiveRepository.countByOwnerIdAndApiaryIdAndSource(userId, apiary.getId(), EBeeSource.SWARM))
                .countNucHives(hiveRepository.countByOwnerIdAndApiaryIdAndSource(userId, apiary.getId(), EBeeSource.NUC))
                .countPackageHives(hiveRepository.countByOwnerIdAndApiaryIdAndSource(userId, apiary.getId(), EBeeSource.PACKAGE))
                .countSplitHives(hiveRepository.countByOwnerIdAndApiaryIdAndSource(userId, apiary.getId(), EBeeSource.SPLIT))
                .countAcquiredHives(hiveRepository.countByOwnerIdAndApiaryIdAndSource(userId, apiary.getId(), EBeeSource.ACQUIRED))
                .countOtherHives(hiveRepository.countByOwnerIdAndApiaryIdAndSource(userId, apiary.getId(), EBeeSource.OTHER))

                .oldestHive(oldestHive.isEmpty() ? "" : oldestHive.get(0).getName())
                .youngestHive(youngestHive.isEmpty() ? "" : youngestHive.get(0).getName())

                // Hive honey
                .mostHoneyHiveLastYear(mostHoneyHiveLY.getHive())
                .mostHoneyLastYear(mostHoneyHiveLY.getMostHiveWeight())
                .totalHoneyLastYear(mostHoneyHiveLY.getTotal())
                .mostHoneyHiveCurrentYear(mostHoneyHiveCY.getHive())
                .mostHoneyCurrentYear(mostHoneyHiveCY.getMostHiveWeight())
                .totalHoneyCurrentYear(mostHoneyHiveCY.getTotal())
                // Hive Wax
                .mostWaxHiveLastYear(mostWaxHiveLY.getHive())
                .mostWaxLastYear(mostWaxHiveLY.getMostHiveWeight())
                .totalWaxLastYear(mostWaxHiveLY.getTotal())
                .mostWaxHiveCurrentYear(mostWaxHiveCY.getHive())
                .mostWaxCurrentYear(mostWaxHiveCY.getMostHiveWeight())
                .totalWaxCurrentYear(mostWaxHiveCY.getTotal())
                // Hive propolis
                .mostPropolisHiveLastYear(mostPropolisHiveLY.getHive())
                .mostPropolisLastYear(mostPropolisHiveLY.getMostHiveWeight())
                .totalPropolisLastYear(mostPropolisHiveLY.getTotal())
                .mostPropolisHiveCurrentYear(mostPropolisHiveCY.getHive())
                .mostPropolisCurrentYear(mostPropolisHiveCY.getMostHiveWeight())
                .totalPropolisCurrentYear(mostPropolisHiveCY.getTotal())
                // Hive pollen
                .mostPollenHiveLastYear(mostPollenHiveLY.getHive())
                .mostPollenLastYear(mostPollenHiveLY.getMostHiveWeight())
                .totalPollenLastYear(mostPollenHiveLY.getTotal())
                .mostPollenHiveCurrentYear(mostPollenHiveCY.getHive())
                .mostPollenCurrentYear(mostPollenHiveCY.getMostHiveWeight())
                .totalPollenCurrentYear(mostPollenHiveCY.getTotal())
                // Hive jelly
                .mostJellyHiveLastYear(mostJellyHiveLY.getHive())
                .mostJellyLastYear(mostJellyHiveLY.getMostHiveWeight())
                .totalJellyLastYear(mostJellyHiveLY.getTotal())
                .mostJellyHiveCurrentYear(mostJellyHiveCY.getHive())
                .mostJellyCurrentYear(mostJellyHiveCY.getMostHiveWeight())
                .totalJellyCurrentYear(mostJellyHiveCY.getTotal())

                .hivesStatistics(getHiveStatistics(userId, apiary.getId()))

                .build();
    }

    /**
     * Get hive statistics
     * @param userId user id
     * @param apiaryId apiary id
     * @return return detailed statistics for single hive
     */
    private List<HiveStatisticsDto> getHiveStatistics(Long userId, Long apiaryId) {
        var hives = hiveRepository.findByOwnerIdAndApiaryIdOrderByEstablishmentAsc(userId, apiaryId);
        return hives
                .stream()
                .map(hive -> new HiveStatisticsDto(hive.getId(), hive.getName(), hive.getSource(), hive.getColor(), getHiveInspections(hive.getId())))
                .toList();
    }

    /**
     * Find and map inspections for single hive
     * @param hiveId hive id
     * @return list of inspections for single hive
     */
    private List<InspectionDetailDto> getHiveInspections(Long hiveId) {
        return inspectionRepository.findByHiveIdOrderById(hiveId).stream().map(modelMapper::convertInspectionEntity).toList();
    }

    /**
     * Find the most honey productive hive
     * @param hives list of hives
     * @param year of collection
     * @return pair of beehive name and weight of honey
     */
    private HiveProductionInfo mostHoneyProductiveHive(List<HiveEntity> hives, int year) {
        double honey = 0;
        double total = 0;
        String hiveName = "-";
        for (var hive : hives) {
            var hiveHoney =  calculateSingleHiveHoneyProductionInYear(hive.getId(), year);
            total += hiveHoney;
            if (honey < hiveHoney) {
                honey = hiveHoney;
                hiveName = hive.getName();
            }
        }
        return new HiveProductionInfo(hiveName, roundNumber(honey), roundNumber(total));
    }

    /**
     * Find the most productive hive by product
     * @param hives list of hives
     * @param year of collection
     * @param product type
     * @return pair of beehive name and weight of product
     */
    private HiveProductionInfo mostProductiveHive(List<HiveEntity> hives, int year, EHarvestProduct product) {
        double mostHiveWeight = 0;
        double total = 0;
        String hiveName = "-";
        for (var hive : hives) {
            var hiveProduct =  calculateSingleHiveProductWeightInYear(hive.getId(), year, product);
            total += hiveProduct;
            if (mostHiveWeight < hiveProduct) {
                mostHiveWeight = hiveProduct;
                hiveName = hive.getName();
            }
        }
        return new HiveProductionInfo(hiveName, roundNumber(mostHiveWeight), roundNumber(total));
    }

    /**
     * Calculate honey weight in single hive
     * @param hiveId hive id
     * @param year year of collection
     * @return weight of honey in hive for specific year
     */
    private double calculateSingleHiveHoneyProductionInYear(Long hiveId, int year) {
        var inspections = inspectionRepository.findByHiveIdOrderById(hiveId);
        inspections = inspections.stream().filter(inspection -> inspection.getInspectionDate().getYear() == year).toList();
        var harvest = inspections.stream().map(InspectionEntity::getHarvest).filter(hiveHarvestEntity -> hiveHarvestEntity.getProduct().equals(EHarvestProduct.HONEY)).toList();
        double honey = 0.0;
        for (var item : harvest) {
            switch (item.getProductUnit()) {
                case GRAM -> honey += item.getProductQuantity() / 1000.0;
                case KILOGRAM -> honey += item.getProductQuantity();
                case MILLILITER -> honey += item.getProductQuantity() / 1000.0 * 1.425;
                case LITER -> honey += item.getProductQuantity() * 1.425;
            }
        }
        return roundNumber(honey);
    }

    /**
     * Calculate product weight in single hive
     * @param hiveId hive id
     * @param year year of collection
     * @param product type
     * @return weight of product in hive for specific year
     */
    private double calculateSingleHiveProductWeightInYear(Long hiveId, int year, EHarvestProduct product) {
        var inspections = inspectionRepository.findByHiveIdOrderById(hiveId);
        inspections = inspections.stream().filter(inspection -> inspection.getInspectionDate().getYear() == year).toList();
        var harvest = inspections.stream().map(InspectionEntity::getHarvest).filter(hiveHarvestEntity -> hiveHarvestEntity.getProduct().equals(product)).toList();
        double weight = 0.0;
        for (var item : harvest) {
            switch (item.getProductUnit()) {
                case GRAM -> weight += item.getProductQuantity() / 1000.0;
                case KILOGRAM -> weight += item.getProductQuantity();
            }
        }
        return roundNumber(weight);
    }

    /**
     * Calculate total weight of honey collected in specific year
     * @param year year to calculate weight
     * @return double value represent weight of collected honey in specific year in kilograms
     */
    private double calculateHoneyTotal(int year) {
        double total = 0.0;
        var kg = inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(year, EHarvestProduct.HONEY, EUnitsAndDoses.KILOGRAM);
        total += kg == null ? 0 : kg;
        var g = inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(year, EHarvestProduct.HONEY, EUnitsAndDoses.GRAM);
        total += g == null ? 0 : (g / 1000);
        var l = inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(year, EHarvestProduct.HONEY, EUnitsAndDoses.LITER);
        total += l == null ? 0 : (l * 1.425);
        var ml = inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(year, EHarvestProduct.HONEY, EUnitsAndDoses.MILLILITER);
        total += ml == null ? 0 : (ml / 1000 * 1.425);
        return roundNumber(total);
    }

    /**
     * Calculate total weight of product in specific year
     * @param year year to calculate
     * @param product type of product
     * @return total weight in kilograms collected in specific year
     */
    private double calculateTotalProduct(int year, EHarvestProduct product) {
        double total = 0.0;
        var kg = inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(year, product, EUnitsAndDoses.KILOGRAM);
        total += kg == null ? 0 : kg;
        var g = inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(year, product, EUnitsAndDoses.GRAM);
        total += g == null ? 0 : (g / 1000);
        return roundNumber(total);
    }

    /**
     * Round double value to 3 decimal places
     * @param value value to round
     * @return rounded double number
     */
    private double roundNumber(double value) {
        return Double.parseDouble(String.format("%.3f", value));
    }
}

/**
 * Class for statistics summary of single hive
 */
@Data
@AllArgsConstructor
class HiveProductionInfo {
    private String hive;
    private double mostHiveWeight;
    private double total;
}

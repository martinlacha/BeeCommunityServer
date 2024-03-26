package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.GeneralStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.repository.*;
import cz.zcu.kiv.server.beecommunity.services.IStatsService;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            stats.setAverageDailyNews((double) dailyCountsNews.stream().mapToInt(Integer::intValue).sum() /dailyCountsNews.size());
        }

        // Community
        stats.setCountPosts((int) communityRepository.count());
        stats.setCountPublicPosts(communityRepository.countByAccess(CommunityEnums.EAccess.PUBLIC));
        stats.setTopPostUser(communityRepository.findTopUserByPostCount());
        stats.setNewsCountOverviewMap(modelMapper.convertObjectTimelineCounts(communityRepository.findCountPostsByCreatedDate()));
        var dailyCountsPosts = communityRepository.countsDailyPosts();
        if (!dailyCountsPosts.isEmpty()) {
            stats.setAverageDailyPosts((double) dailyCountsPosts.stream().mapToInt(Integer::intValue).sum() /dailyCountsPosts.size());
        }

        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }
}

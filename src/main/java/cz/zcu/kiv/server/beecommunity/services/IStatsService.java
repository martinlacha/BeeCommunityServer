package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.FriendsStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.GeneralStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.UserDetailStatisticsDto;
import org.springframework.http.ResponseEntity;

public interface IStatsService {
    ResponseEntity<GeneralStatisticsDto> getGeneralStatistics();

    ResponseEntity<FriendsStatisticsDto> getFriendsStatistics();

    ResponseEntity<UserDetailStatisticsDto> getPersonalStatistics();

    ResponseEntity<UserDetailStatisticsDto> getFriendDetailStatistics(String email);
}

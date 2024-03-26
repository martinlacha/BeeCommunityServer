package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.GeneralStatisticsDto;
import org.springframework.http.ResponseEntity;

public interface IStatsService {
    ResponseEntity<GeneralStatisticsDto> getGeneralStatistics();
}

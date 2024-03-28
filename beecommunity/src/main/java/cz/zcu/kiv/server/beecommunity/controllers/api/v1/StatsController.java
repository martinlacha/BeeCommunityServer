package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.FriendsStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.GeneralStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.UserDetailStatisticsDto;
import cz.zcu.kiv.server.beecommunity.services.IStatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for statistics endpoints
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/stats")
@Tag(name = "Statistics")
@AllArgsConstructor
public class StatsController {
    private final IStatsService apiaryService;

    /**
     * General statistics from all users in system
     * @return general statistics
     */
    @GetMapping("/general")
    ResponseEntity<GeneralStatisticsDto> getGeneralStatistics() {
        return apiaryService.getGeneralStatistics();
    }

    /**
     * Return friends statistics
     * @return friends statistics
     */
    @GetMapping("/friends")
    ResponseEntity<FriendsStatisticsDto> getFriendsStatistics() {
        return apiaryService.getFriendsStatistics();
    }

    /**
     * Return personal statistics
     * @return detailed personal statistics
     */
    @GetMapping("/personal")
    ResponseEntity<UserDetailStatisticsDto> getPersonalStatistics() {
        return apiaryService.getPersonalStatistics();
    }

    /**
     * Return detailed friend statistics
     * @return detailed statistics of friend
     */
    @GetMapping("/friend")
    ResponseEntity<UserDetailStatisticsDto> getFriendDetailStatistics(@RequestParam Long userId) {
        return apiaryService.getFriendDetailStatistics(userId);
    }
}

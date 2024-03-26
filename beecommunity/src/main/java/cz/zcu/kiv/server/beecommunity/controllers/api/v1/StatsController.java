package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.GeneralStatisticsDto;
import cz.zcu.kiv.server.beecommunity.services.IStatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * Return general statistics
     * @return list of apiaries
     */
    @GetMapping
    ResponseEntity<GeneralStatisticsDto> getApiaries() {
        return apiaryService.getGeneralStatistics();
    }
}

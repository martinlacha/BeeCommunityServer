package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.apiary.ApiaryDto;
import cz.zcu.kiv.server.beecommunity.services.IApiaryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for apiaries endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/apiary")
@Tag(name = "Apiary")
@AllArgsConstructor
public class ApiaryController {
    private final IApiaryService apiaryService;

    /**
     * Return list of apiaries
     * @return list of apiaries
     */
    @GetMapping
    ResponseEntity<List<ApiaryDto>> getApiaries() {
        return apiaryService.getApiaries();
    }

    /**
     * Return list of friend apiaries
     * @return list of apiaries if users are friends
     */
    @GetMapping("/friend")
    ResponseEntity<List<ApiaryDto>> getFriendApiaries(@RequestParam String email) {
        return apiaryService.getFriendApiaries(email);
    }

    /**
     * Create new apiary for user
     * @param apiaryDto dto with information and image
     * @return status code of operation result
     */
    @PostMapping
    ResponseEntity<Void> createApiary(@ModelAttribute @Valid ApiaryDto apiaryDto) {
        return apiaryService.createApiary(apiaryDto);
    }

    /**
     * Get apiary image if was uploaded
     * @param apiaryId apiary id
     * @return byte array of image
     */
    @GetMapping("/image")
    ResponseEntity<byte[]> getPostImage(@RequestParam Long apiaryId) {
        return apiaryService.getApiaryImage(apiaryId);
    }

    /**
     * Delete apiary by id
     * @param apiaryId apiary id
     * @return status code of operation result
     */
    @DeleteMapping
    ResponseEntity<Void> deleteApiaryById(@RequestParam Long apiaryId) {
        return apiaryService.deleteApiary(apiaryId);
    }

    /**
     * Update apiary by id
     * @param apiaryDto dto with information and image
     * @return status code of operation result
     */
    @PutMapping
    ResponseEntity<Void> updateApiaryById(@ModelAttribute @Valid ApiaryDto apiaryDto) {
        return apiaryService.updateApiary(apiaryDto);
    }

    /**
     * Return list of apiaries
     * @return list of apiaries
     */
    @GetMapping("/detail")
    ResponseEntity<ApiaryDto> getApiaryDetail(@RequestParam Long apiaryId) {
        return apiaryService.getApiaryDetail(apiaryId);
    }
}

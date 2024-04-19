package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.services.IQueenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for queen endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/queen")
@Tag(name = "Queen")
@AllArgsConstructor
public class QueenController {
    private final IQueenService queenService;

    /**
     * Get queen image if was uploaded
     * @param hiveId queen id
     * @return byte array of image
     */
    @GetMapping("/image")
    ResponseEntity<byte[]> getQueenImage(@RequestParam Long hiveId) {
        return queenService.getQueenImage(hiveId);
    }

}

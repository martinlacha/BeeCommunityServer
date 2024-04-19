package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for server endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/server")
@Tag(name = "Server")
public class ServerController {

    /**
     * Endpoint to test the connection is established
     * @return HttpStatus.OK if connection is established
     */
    @GetMapping("/test-connection")
    ResponseEntity<Void> testConnection() {
        log.info("Successful connection test");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

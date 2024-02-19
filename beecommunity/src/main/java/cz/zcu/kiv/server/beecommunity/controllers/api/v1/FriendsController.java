package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.services.IFriendService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for friends endpoints
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/friends")
@Tag(name = "User")
@AllArgsConstructor
public class FriendsController {

    private final IFriendService friendService;

    @GetMapping("/find")
    ResponseEntity<List<FoundUserDto>> getUserInfo(@RequestParam String name) {
        return friendService.findUsers(name);
    }
}

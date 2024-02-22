package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.services.IFriendService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Find all accounts that email, name or surname contain text
     * @param name text that account has to contain in email, name or surname
     * @return list of found users
     */
    @GetMapping("/find")
    ResponseEntity<List<FoundUserDto>> findUsers(@RequestParam String name) {
        return friendService.findUsers(name);
    }

    /**
     * Send new request for friend to another user
     * @param email account to create friendship with
     * @return status code of operation result
     */
    @PostMapping("/add")
    ResponseEntity<Void> sendFriendRequest(@RequestParam String email) {
        return friendService.sendFriendRequest(email);
    }

    /**
     * Accept friend request from another user
     * @param email account to create friendship with
     * @return status code of operation result
     */
    @PostMapping("/accept")
    ResponseEntity<Void> acceptFriendRequest(@RequestParam String email) {
        return friendService.acceptFriendRequest(email);
    }

    /**
     * Reject friend request
     * @param email account to reject friendship with
     * @return status code of operation result
     */
    @PostMapping("/reject")
    ResponseEntity<Void> rejectFriendRequest(@RequestParam String email) {
        return friendService.rejectFriendRequest(email);
    }

    /**
     * Remove friend of pending request from another user
     * @param email account to remove friendship
     * @return status code of operation result
     */
    @DeleteMapping("/remove")
    ResponseEntity<Void> removeFriendOrRequest(@RequestParam String email) {
        return friendService.removeFriendOrRequest(email);
    }

    /**
     * Block user account
     * @param email account to block
     * @return status code of operation result
     */
    @PostMapping("/block")
    ResponseEntity<Void> blockUser(@RequestParam String email) {
        return friendService.blockUser(email);
    }

    /**
     * Unblock user account
     * @param email account to unblock
     * @return status code of operation result
     */
    @PostMapping("/unblock")
    ResponseEntity<Void> unblockUser(@RequestParam String email) {
        return friendService.unblockUser(email);
    }

    /**
     * Return all users friends
     * @return list of friends
     */
    @GetMapping("/get")
    ResponseEntity<List<FoundUserDto>> getMyFriends() {
        return friendService.getMyFriends();
    }

    /**
     * Return all users friends
     * @return list of friends
     */
    @GetMapping("/pending")
    ResponseEntity<List<FoundUserDto>> getPendingRequests() {
        return friendService.getPendingRequests();
    }

    /**
     * Return all users friends
     * @return list of friends
     */
    @GetMapping("/blocked")
    ResponseEntity<List<FoundUserDto>> getBlockedUsers() {
        return friendService.getBlockedUsers();
    }
}

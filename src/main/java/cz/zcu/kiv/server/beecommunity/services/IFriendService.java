package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFriendService {
    ResponseEntity<List<FoundUserDto>> findUsers(String name);

    ResponseEntity<List<FoundUserDto>> getMyFriends();

    ResponseEntity<List<FoundUserDto>> getPendingRequests();

    ResponseEntity<List<FoundUserDto>> getBlockedUsers();

    ResponseEntity<Void> sendFriendRequest(@NotNull String email);

    ResponseEntity<Void> removeFriendOrRequest(@NotNull String email);

    ResponseEntity<Void> blockUser(@NotNull String email);

    ResponseEntity<Void> unblockUser(@NotNull String email);

    ResponseEntity<Void> acceptFriendRequest(String email);

    ResponseEntity<Void> rejectFriendRequest(String email);
}

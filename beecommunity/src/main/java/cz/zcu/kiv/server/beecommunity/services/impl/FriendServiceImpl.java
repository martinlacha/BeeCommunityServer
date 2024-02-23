package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.FriendshipEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.FriendshipRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.services.IFriendService;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements IFriendService {

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    private final ObjectMapper modelMapper;

    /**
     * Find all users that name, surname or email contain text ignoring case
     * Remove all users which don't have user info yet
     * @param name text that user has to contain to return
     * @return list of users with info about name, surname and email
     */
    @Override
    public ResponseEntity<List<FoundUserDto>> findUsers(String name) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        List<UserEntity> list = userRepository
                .findByEmailContainsIgnoreCaseOrUserInfoNameContainsIgnoreCaseOrUserInfoSurnameContainsIgnoreCase(
                    name, name, name
                ).stream()
                .filter(userEntity -> (userEntity.getUserInfo() != null && !userEntity.getEmail().equals(user.getEmail()))).toList();
        list = list.stream().filter(userEntity -> getFriendship(user.getId(), userEntity.getId()).isEmpty()).toList();
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertListUserEntity(list));
    }

    /**
     * Find all user friends
     * @return list of found friends
     */
    @Override
    public ResponseEntity<List<FoundUserDto>> getMyFriends() {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        var friends = friendshipRepository.findBySenderIdAndStatusOrReceiverIdAndStatus(
                user.getId(), FriendshipEnums.EStatus.FRIEND, user.getId(), FriendshipEnums.EStatus.FRIEND);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertListFriendship(friends, user.getId()));
    }

    /**
     * Get all pending request send to me
     * @return list of users that send me request for friendship
     */
    @Override
    public ResponseEntity<List<FoundUserDto>> getPendingRequests() {
        return getListByStatus(FriendshipEnums.EStatus.PENDING, false);
    }

    /**
     * Get all blocked users
     * @return return list of block users
     */
    @Override
    public ResponseEntity<List<FoundUserDto>> getBlockedUsers() {
        return getListByStatus(FriendshipEnums.EStatus.BLOCKED, true);
    }


    /**
     * Send request for friendship to another user
     * @param email user to send request
     * @return status of operation result
     */
    @Override
    public ResponseEntity<Void> sendFriendRequest(@NotNull String email) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        Optional<UserEntity> newFriend = userRepository.findByEmail(email);
        if (newFriend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (email.equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else if (friendshipRepository.existsBySenderIdAndReceiverId(newFriend.get().getId(), user.getId()) ||
                friendshipRepository.existsBySenderIdAndReceiverId(user.getId(), newFriend.get().getId())) {
            // Some friendship already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        FriendshipEntity pendingFriendship = new FriendshipEntity();
        pendingFriendship.setSender(user);
        pendingFriendship.setReceiver(newFriend.get());
        pendingFriendship.setStatus(FriendshipEnums.EStatus.PENDING);
        friendshipRepository.saveAndFlush(pendingFriendship);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Remove user from friend list or pending requests
     * @param email friend or user which send request
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> removeFriendOrRequest(@NotNull String email) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        Optional<UserEntity> removeFriend = userRepository.findByEmail(email);
        if (removeFriend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<FriendshipEntity> friendshipToDelete = getFriendship(user.getId(), removeFriend.get().getId());
        if (friendshipToDelete.isEmpty()) {
            // Friendship not exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (FriendshipEnums.EStatus.BLOCKED.equals(friendshipToDelete.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        friendshipRepository.delete(friendshipToDelete.get());
        friendshipRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Block another user account
     * @param email user that will be blocked for current user
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> blockUser(@NotNull String email) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        Optional<UserEntity> userToBlock = userRepository.findByEmail(email);
        if (userToBlock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<FriendshipEntity> friendshipToBlock = getFriendship(user.getId(), userToBlock.get().getId());
        if (friendshipToBlock.isEmpty()) {
            // Friendship not exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (FriendshipEnums.EStatus.BLOCKED.equals(friendshipToBlock.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        friendshipToBlock.get().setSender(user);
        friendshipToBlock.get().setReceiver(userToBlock.get());
        friendshipToBlock.get().setStatus(FriendshipEnums.EStatus.BLOCKED);
        friendshipRepository.saveAndFlush(friendshipToBlock.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Unblock another user account
     * @param email user that will be unblocked for current user
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> unblockUser(@NotNull String email) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        Optional<UserEntity> userToBlock = userRepository.findByEmail(email);
        if (userToBlock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<FriendshipEntity> friendshipToBlock = getFriendship(user.getId(), userToBlock.get().getId());
        if (friendshipToBlock.isEmpty()) {
            // Friendship not exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (!FriendshipEnums.EStatus.BLOCKED.equals(friendshipToBlock.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        friendshipToBlock.get().setStatus(FriendshipEnums.EStatus.FRIEND);
        friendshipRepository.saveAndFlush(friendshipToBlock.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Accept friend request and update friendship record
     * @param email new friend account
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> acceptFriendRequest(String email) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        Optional<UserEntity> friend = userRepository.findByEmail(email);
        if (friend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var friendship = getFriendship(user.getId(), friend.get().getId());
        if (friendship.isEmpty() || !FriendshipEnums.EStatus.PENDING.equals(friendship.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        friendship.get().setStatus(FriendshipEnums.EStatus.FRIEND);
        friendshipRepository.saveAndFlush(friendship.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Reject friend request and remove record from friendship table
     * @param email rejected account
     * @return  status code of operation result
     */
    @Override
    public ResponseEntity<Void> rejectFriendRequest(String email) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        Optional<UserEntity> rejectedUser = userRepository.findByEmail(email);
        if (rejectedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var friendship = getFriendship(user.getId(), rejectedUser.get().getId());
        if (friendship.isEmpty() || !FriendshipEnums.EStatus.PENDING.equals(friendship.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        friendshipRepository.delete(friendship.get());
        friendshipRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Get list of dto of friends by friendship status, and it takes from database from receiver or sender
     * @param status filter requests by status
     * @param fromSender true return all my requests, false return all
     * @return return list of found users filtered by status
     */
    private ResponseEntity<List<FoundUserDto>> getListByStatus(FriendshipEnums.EStatus status, boolean fromSender) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        var list = fromSender ?
                friendshipRepository.findBySenderIdAndStatus(user.getId(), status) :
                friendshipRepository.findByReceiverIdAndStatus(user.getId(), status);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertListFriendship(list, user.getId()));
    }

    /**
     * Find friendship between two users
     * @param myAccount account which send request
     * @param anotherAccount receiver account
     * @return friendship entity if exists
     */
    private Optional<FriendshipEntity> getFriendship(Long myAccount, Long anotherAccount) {
        Optional<FriendshipEntity> friendshipToDelete =
                friendshipRepository.findBySenderIdAndReceiverId(myAccount, anotherAccount);
        if (friendshipToDelete.isEmpty()) {
            friendshipToDelete =
                    friendshipRepository.findBySenderIdAndReceiverId(anotherAccount, myAccount);
        }
        return friendshipToDelete;
    }
}

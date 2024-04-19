package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.FriendshipEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.FriendshipRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendServiceImplTest {

    @Mock
    private ObjectMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipUtils friendshipUtils;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendServiceImpl friendService;

    private UserEntity user;

    private final TestData testData = new TestData();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        user = testData.getUser1();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void testFindUsers() {
        when(userRepository.findByEmailContainsIgnoreCaseOrUserInfoNameContainsIgnoreCaseOrUserInfoSurnameContainsIgnoreCase(
                any(), any(), any())).thenReturn(Collections.emptyList());
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any()))
                .thenReturn(false);
        ResponseEntity<List<FoundUserDto>> expectedResponse = ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());

        ResponseEntity<List<FoundUserDto>> actualResponse = friendService.findUsers("test");
        verify(userRepository, times(1))
                .findByEmailContainsIgnoreCaseOrUserInfoNameContainsIgnoreCaseOrUserInfoSurnameContainsIgnoreCase(
                        "test", "test", "test");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetMyFriends() {
        List<FriendshipEntity> friendships = new ArrayList<>();
        when(friendshipRepository.findBySenderIdAndStatusOrReceiverIdAndStatus(anyLong(), any(), anyLong(), any()))
                .thenReturn(friendships);
        ResponseEntity<List<FoundUserDto>> expectedResponse = ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        ResponseEntity<List<FoundUserDto>> actualResponse = friendService.getMyFriends();

        verify(friendshipRepository, times(1))
                .findBySenderIdAndStatusOrReceiverIdAndStatus(
                        user.getId(),
                        FriendshipEnums.EStatus.FRIEND,
                        user.getId(),
                        FriendshipEnums.EStatus.FRIEND);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetPendingRequests() {
        List<FriendshipEntity> friendships = new ArrayList<>();
        when(friendshipRepository.findByReceiverIdAndStatus(anyLong(), eq(FriendshipEnums.EStatus.PENDING)))
                .thenReturn(friendships);
        ResponseEntity<List<FoundUserDto>> expectedResponse = ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());

        ResponseEntity<List<FoundUserDto>> actualResponse = friendService.getPendingRequests();

        verify(friendshipRepository, times(1))
                .findByReceiverIdAndStatus(user.getId(), FriendshipEnums.EStatus.PENDING);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetBlockedUsers() {
        List<FriendshipEntity> friendships = new ArrayList<>();
        when(friendshipRepository.findByReceiverIdAndStatus(anyLong(), eq(FriendshipEnums.EStatus.BLOCKED)))
                .thenReturn(friendships);

        ResponseEntity<List<FoundUserDto>> expectedResponse = ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        ResponseEntity<List<FoundUserDto>> actualResponse = friendService.getBlockedUsers();

        verify(friendshipRepository, times(1))
                .findBySenderIdAndStatus(eq(user.getId()), eq(FriendshipEnums.EStatus.BLOCKED));
        verify(modelMapper, times(1)).convertListFriendship(anyList(), eq(user.getId()));
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testSendFriendRequest_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.sendFriendRequest("nonexistent@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testSendFriendRequest_Conflict() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        ResponseEntity<Void> response = friendService.sendFriendRequest(user.getEmail());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testSendFriendRequest_BadRequest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);

        ResponseEntity<Void> response = friendService.sendFriendRequest("nonexistent@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testSendFriendRequest_NotAcceptable() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.PENDING))).thenReturn(true);

        ResponseEntity<Void> response = friendService.sendFriendRequest("nonexistent@example.com");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.PENDING));
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testSendFriendRequest_Locked() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.PENDING))).thenReturn(false);
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.BLOCKED))).thenReturn(true);

        ResponseEntity<Void> response = friendService.sendFriendRequest("nonexistent@example.com");

        assertEquals(HttpStatus.LOCKED, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.PENDING));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.BLOCKED));
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testSendFriendRequest_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.PENDING))).thenReturn(false);
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.BLOCKED))).thenReturn(false);

        ResponseEntity<Void> response = friendService.sendFriendRequest("nonexistent@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.PENDING));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.BLOCKED));
        verify(friendshipRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testRemoveFriendOrRequest_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.removeFriendOrRequest("nonexistent@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).delete(any());
        verify(friendshipRepository, never()).flush();
    }

    @Test
    void testRemoveFriendOrRequest_Conflict() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.removeFriendOrRequest("nonexistent@example.com");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).delete(any());
        verify(friendshipRepository, never()).flush();
    }

    @Test
    void testRemoveFriendOrRequest_BadRequest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(FriendshipEntity.builder().status(FriendshipEnums.EStatus.BLOCKED).build()));

        ResponseEntity<Void> response = friendService.removeFriendOrRequest("nonexistent@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).delete(any());
        verify(friendshipRepository, never()).flush();
    }

    @Test
    void testRemoveFriendOrRequest_Success() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.FRIEND).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.removeFriendOrRequest("nonexistent@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).delete(eq(friendship));
        verify(friendshipRepository, times(1)).flush();
    }

    @Test
    void testBlockUser_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.blockUser("nonexistent@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testBlockUser_Conflict() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.blockUser("nonexistent@example.com");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(2)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testBlockUser_BadRequest() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.BLOCKED).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.blockUser("nonexistent@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testBlockUser_Success() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.FRIEND).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.blockUser("nonexistent@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, times(1)).saveAndFlush(eq(friendship));
    }

    @Test
    void testUnblockUser_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.unblockUser("nonexistent@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUnblockUser_Conflict() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.unblockUser("nonexistent@example.com");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(2)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUnblockUser_BadRequest_Friend() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.FRIEND).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.unblockUser("nonexistent@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUnblockUser_BadRequest_Pending() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.PENDING).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.unblockUser("nonexistent@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUnblockUser_Success() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.BLOCKED).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.unblockUser("nonexistent@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, times(1)).saveAndFlush(eq(friendship));
    }

    @Test
    void testAcceptFriendRequest_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.acceptFriendRequest("friend@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testAcceptFriendRequest_BadRequest_EmptyFriendship() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new UserEntity()));

        ResponseEntity<Void> response = friendService.acceptFriendRequest("friend@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(2)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testAcceptFriendRequest_BadRequest_Blocked() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.BLOCKED).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.acceptFriendRequest("friend@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testAcceptFriendRequest_BadRequest_Friend() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.FRIEND).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.acceptFriendRequest("friend@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testAcceptFriendRequest_Success() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.PENDING).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.acceptFriendRequest("friend@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, times(1)).saveAndFlush(eq(friendship));
    }

    /*
    *
    * */

    @Test
    void testRejectFriendRequest_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = friendService.rejectFriendRequest("user@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRejectFriendRequest_BadRequest_EmptyFriendship() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new UserEntity()));

        ResponseEntity<Void> response = friendService.rejectFriendRequest("user@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(2)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRejectFriendRequest_BadRequest_Blocked() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.BLOCKED).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.rejectFriendRequest("user@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRejectFriendRequest_BadRequest_Friend() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.FRIEND).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.rejectFriendRequest("user@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRejectFriendRequest_Success() {
        var friendship = FriendshipEntity.builder().status(FriendshipEnums.EStatus.PENDING).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipRepository.findBySenderIdAndReceiverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(friendship));

        ResponseEntity<Void> response = friendService.rejectFriendRequest("user@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(friendshipRepository, times(1)).findBySenderIdAndReceiverId(any(), any());
        verify(friendshipRepository, times(1)).delete(eq(friendship));
        verify(friendshipRepository, times(1)).flush();
    }
}
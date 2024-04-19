package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.enums.HiveEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.FriendsStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.UserDetailStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.ApiaryRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.HiveRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.services.IFriendService;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StatsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IFriendService friendService;

    @Mock
    private FriendshipUtils friendshipUtils;

    @Mock
    private ApiaryRepository apiaryRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StatsServiceImpl statsService;

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
    void testGetFriendsStatistics() {
        List<FoundUserDto> friends = Collections.emptyList();
        when(friendService.getMyFriends()).thenReturn(ResponseEntity.status(HttpStatus.OK).body(friends));
        ResponseEntity<FriendsStatisticsDto> response = statsService.getFriendsStatistics();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FriendsStatisticsDto.builder().friends(friends).build(), response.getBody());
        verify(friendService, times(1)).getMyFriends();
    }

    @Test
    void testGetPersonalStatistics() {
        ResponseEntity<UserDetailStatisticsDto> response = statsService.getPersonalStatistics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(apiaryRepository, times(1)).countById(eq(user.getId()));
        verify(hiveRepository, times(2)).findByOwnerId(eq(user.getId()));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(user.getId()), eq(HiveEnums.EBeeSource.SWARM));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(user.getId()), eq(HiveEnums.EBeeSource.NUC));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(user.getId()), eq(HiveEnums.EBeeSource.PACKAGE));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(user.getId()), eq(HiveEnums.EBeeSource.SPLIT));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(user.getId()), eq(HiveEnums.EBeeSource.ACQUIRED));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(user.getId()), eq(HiveEnums.EBeeSource.OTHER));
        verify(apiaryRepository, times(1)).findByOwnerIdOrderById(eq(user.getId()));
    }

    @Test
    void testGetFriendDetailStatistics_NotFound() {
        var friend = testData.getUser3();
        when(userRepository.findByEmail(eq(friend.getEmail()))).thenReturn(Optional.empty());

        ResponseEntity<UserDetailStatisticsDto> response = statsService.getFriendDetailStatistics(friend.getEmail());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(eq(friend.getEmail()));
    }

    @Test
    void testGetFriendDetailStatistics_NotFriend() {
        var friend = testData.getUser3();
        when(userRepository.findByEmail(eq(friend.getEmail()))).thenReturn(Optional.of(friend));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(friend.getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        ResponseEntity<UserDetailStatisticsDto> response = statsService.getFriendDetailStatistics(friend.getEmail());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(userRepository, times(1)).findByEmail(eq(friend.getEmail()));
        verify(friendshipUtils, times(1)).isFriendshipStatus(eq(user.getId()), eq(friend.getId()), eq(FriendshipEnums.EStatus.FRIEND));
    }

    @Test
    void testGetFriendDetailStatistics_Success() {
        var friend = testData.getUser3();
        when(userRepository.findByEmail(eq(friend.getEmail()))).thenReturn(Optional.of(friend));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(friend.getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);

        ResponseEntity<UserDetailStatisticsDto> response = statsService.getFriendDetailStatistics(friend.getEmail());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userRepository, times(1)).findByEmail(eq(friend.getEmail()));
        verify(friendshipUtils, times(1)).isFriendshipStatus(eq(user.getId()), eq(friend.getId()), eq(FriendshipEnums.EStatus.FRIEND));
        verify(apiaryRepository, times(1)).countById(eq(friend.getId()));
        verify(hiveRepository, times(2)).findByOwnerId(eq(friend.getId()));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(friend.getId()), eq(HiveEnums.EBeeSource.SWARM));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(friend.getId()), eq(HiveEnums.EBeeSource.NUC));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(friend.getId()), eq(HiveEnums.EBeeSource.PACKAGE));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(friend.getId()), eq(HiveEnums.EBeeSource.SPLIT));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(friend.getId()), eq(HiveEnums.EBeeSource.ACQUIRED));
        verify(hiveRepository, times(1)).countByOwnerIdAndSource(eq(friend.getId()), eq(HiveEnums.EBeeSource.OTHER));
        verify(apiaryRepository, times(1)).findByOwnerIdOrderById(eq(friend.getId()));
    }
}

package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.enums.HiveEnums;
import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.FriendsStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.UserDetailStatisticsDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.*;
import cz.zcu.kiv.server.beecommunity.services.IFriendService;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
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

    @Mock
    private ObjectMapper modelMapper;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private InspectionRepository inspectionRepository;

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
    void testGetGeneralStatistics() {
        when(modelMapper.convertObjectTimelineCounts(any())).thenReturn(Collections.emptyList());
        when(newsRepository.count()).thenReturn(0L);
        when(communityPostRepository.count()).thenReturn(0L);
        when(inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(anyInt(), any(), any())).thenReturn(0.0);
        var response = statsService.getGeneralStatistics();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetGeneralStatistics_NotEmpty_Selects() {
        List<Object[]> apiariesCount = Arrays.asList(
                new Object[]{"Jack", 10},
                new Object[]{"John", 3});
        List<Object[]> hivesCount = Arrays.asList(
                new Object[]{"Lane", 8},
                new Object[]{"Lisa", 4});
        List<Object[]> mostCommonDisease = Arrays.asList(
                new Object[]{InspectionEnums.EDisease.VARROASIS, 8},
                new Object[]{InspectionEnums.EDisease.NOSEMA, 4});
        List<Object[]> mostCommonFood = Arrays.asList(
                new Object[]{InspectionEnums.EFoodType.SUGAR, 8},
                new Object[]{InspectionEnums.EFoodType.POLLEN, 4});
        when(modelMapper.convertObjectTimelineCounts(any())).thenReturn(Collections.emptyList());
        when(newsRepository.count()).thenReturn(0L);
        when(communityPostRepository.count()).thenReturn(0L);
        when(inspectionRepository.sumQuantityByProductAndUnitTypeAndYear(anyInt(), any(), any())).thenReturn(0.0);
        when(apiaryRepository.countApiariesGroupByOwner()).thenReturn(apiariesCount);
        when(hiveRepository.countHivesGroupByOwner()).thenReturn(apiariesCount);
        when(newsRepository.countsDailyNews()).thenReturn(List.of(1, 2));
        when(communityPostRepository.countsDailyPosts()).thenReturn(List.of(1, 2, 3));
        when(apiaryRepository.countApiariesGroupByOwner()).thenReturn(apiariesCount);
        when(inspectionRepository.getMostCommonDisease()).thenReturn(mostCommonDisease);
        when(inspectionRepository.getMostCommonFoodType()).thenReturn(mostCommonFood);
        var response = statsService.getGeneralStatistics();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetPersonalStatistics() {
        ResponseEntity<UserDetailStatisticsDto> response = statsService.getPersonalStatistics();
        when(apiaryRepository.findByOwnerIdOrderById(any())).thenReturn(List.of(testData.getApiaryEntity1()));

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
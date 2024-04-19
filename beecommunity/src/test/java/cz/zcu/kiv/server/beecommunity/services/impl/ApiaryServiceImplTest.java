package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.apiary.ApiaryDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.ApiaryEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.ApiaryRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
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

public class ApiaryServiceImplTest {

    @Mock
    private ApiaryRepository apiaryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipUtils friendshipUtils;

    @Mock
    private ObjectMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserUtils userUtils;

    @InjectMocks
    private ApiaryServiceImpl apiaryService;

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
    void testCreateApiary_Success() {
        ApiaryDto apiaryDto = new ApiaryDto();
        when(modelMapper.convertApiaryDto(apiaryDto)).thenReturn(testData.getApiaryEntity1());

        ResponseEntity<Void> response = apiaryService.createApiary(apiaryDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(apiaryRepository, times(1)).saveAndFlush(any(ApiaryEntity.class));
    }

    @Test
    void testGetApiaries_Success() {
        UserEntity user = new UserEntity();
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(apiaryRepository.findByOwnerIdOrderById(user.getId())).thenReturn(Collections.emptyList());

        ResponseEntity<List<ApiaryDto>> response = apiaryService.getApiaries();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(modelMapper, times(1)).convertApiaryEntityList(any());
    }

    @Test
    void testGetFriendApiaries_UserNotFound() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<List<ApiaryDto>> response = apiaryService.getFriendApiaries(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(apiaryRepository);
    }

    @Test
    void testGetFriendApiaries_BadRequest_NotFriend() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        ResponseEntity<List<ApiaryDto>> response = apiaryService.getFriendApiaries(email);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(eq(email));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
        verifyNoInteractions(apiaryRepository);
    }

    @Test
    void testGetFriendApiaries_BadRequest_Success() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testData.getUser2()));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);

        ResponseEntity<List<ApiaryDto>> response = apiaryService.getFriendApiaries(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByEmail(eq(email));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
        verify(apiaryRepository, times(1)).findByOwnerIdOrderById(any());
    }

    @Test
    void testGetApiaryImage_ApiaryNotFound() {
        when(apiaryRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = apiaryService.getApiaryImage(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(any());
    }

    @Test
    void testGetApiaryImage_NotFound() {
        var apiary = testData.getApiaryEntity1();
        apiary.setOwner(testData.getUser3());
        when(apiaryRepository.findById(any())).thenReturn(Optional.of(apiary));

        ResponseEntity<byte[]> response = apiaryService.getApiaryImage(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(any());
    }

    @Test
    void testGetApiaryImage_ImageNotFound() {
        var apiary = testData.getApiaryEntity1();
        apiary.setOwner(testData.getUser1());
        when(apiaryRepository.findById(any())).thenReturn(Optional.of(apiary));

        ResponseEntity<byte[]> response = apiaryService.getApiaryImage(apiary.getId());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(any());
    }

    @Test
    void testGetApiaryImage_Success() {
        var apiary = testData.getApiaryEntity1();
        apiary.setOwner(user);
        apiary.setImage("image data".getBytes());
        when(apiaryRepository.findById(any())).thenReturn(Optional.of(apiary));

        ResponseEntity<byte[]> response = apiaryService.getApiaryImage(apiary.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(any());
    }

    @Test
    void testDeleteApiary_ApiaryNotFound() {
        Long apiaryId = 1L;
        when(apiaryRepository.findById(apiaryId)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = apiaryService.deleteApiary(apiaryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(apiaryRepository, never()).deleteById(apiaryId);
        verify(apiaryRepository, never()).flush();
    }

    @Test
    void testDeleteApiary_UserNotAuthorized() {
        var apiary = testData.getApiaryEntity1();
        apiary.setOwner(testData.getUser2());
        when(apiaryRepository.findById(any())).thenReturn(Optional.of(apiary));

        ResponseEntity<Void> response = apiaryService.deleteApiary(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(apiaryRepository, never()).deleteById(any());
        verify(apiaryRepository, never()).flush();
    }

    @Test
    void testDeleteApiary_Success() {
        var apiary = testData.getApiaryEntity1();
        apiary.setOwner(user);
        when(apiaryRepository.findById(apiary.getId())).thenReturn(Optional.of(apiary));

        ResponseEntity<Void> response = apiaryService.deleteApiary(apiary.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(apiaryRepository, times(1)).deleteById(eq(apiary.getId()));
        verify(apiaryRepository, times(1)).flush();
    }

    @Test
    void testUpdateApiary_ApiaryNotFound() {
        ApiaryDto apiaryDto = testData.getApiaryDto1();
        when(apiaryRepository.findById(apiaryDto.getId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = apiaryService.updateApiary(apiaryDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(apiaryRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateApiary_UserNotAuthorized() {
        var apiary = testData.getApiaryEntity1();
        apiary.setOwner(testData.getUser3());
        when(apiaryRepository.findById(eq(apiary.getId()))).thenReturn(Optional.of(apiary));

        ResponseEntity<Void> response = apiaryService.updateApiary(testData.getApiaryDto1());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(apiaryRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateApiary_Success() {
        var apiary = testData.getApiaryEntity2();
        apiary.setOwner(user);
        when(apiaryRepository.findById(eq(apiary.getId()))).thenReturn(Optional.of(apiary));

        ResponseEntity<Void> response = apiaryService.updateApiary(testData.getApiaryDto2());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiary.getName(), apiary.getName());
        verify(apiaryRepository, times(1)).saveAndFlush(apiary);
    }

    @Test
    void testGetApiaryDetail_ApiaryNotFound() {
        Long apiaryId = 1L;
        when(apiaryRepository.findById(apiaryId)).thenReturn(Optional.empty());

        ResponseEntity<ApiaryDto> response = apiaryService.getApiaryDetail(apiaryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(friendshipUtils, never()).isFriendshipStatus(anyLong(), anyLong(), any());
    }

    @Test
    void testGetApiaryDetail_UserNotAuthorized() {
        var apiary = testData.getApiaryEntity2();
        apiary.setOwner(testData.getUser3());
        when(apiaryRepository.findById(eq(apiary.getId()))).thenReturn(Optional.of(apiary));
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any())).thenReturn(false);

        ResponseEntity<ApiaryDto> response = apiaryService.getApiaryDetail(apiary.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetApiaryDetail_Success() {
        var apiary = testData.getApiaryEntity2();
        apiary.setOwner(user);
        when(apiaryRepository.findById(eq(apiary.getId()))).thenReturn(Optional.of(apiary));
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any())).thenReturn(true);
        when(modelMapper.convertApiaryEntity(any())).thenReturn(testData.getApiaryDto2());

        ResponseEntity<ApiaryDto> response = apiaryService.getApiaryDetail(apiary.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData.getApiaryDto2(), response.getBody());
    }
}

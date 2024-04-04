package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.QueenEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserInfoEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.HiveRepository;
import cz.zcu.kiv.server.beecommunity.testData.ObjectMapperTestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
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

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueenServiceImplTest {

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private FriendshipUtils friendshipUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private QueenServiceImpl queenServiceImpl;

    private ObjectMapperTestData testData = new ObjectMapperTestData();

    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        user = UserEntity
                .builder()
                .id(1L)
                .userInfo(UserInfoEntity.builder().build())
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void testGetQueenImage_InvalidHiveId() {
        long hiveId = 1L;
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = queenServiceImpl.getQueenImage(hiveId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository).findById(hiveId);
        verifyNoMoreInteractions(hiveRepository, friendshipUtils);
    }

    @Test
    void testGetQueenImage_NotFriendsAndNotUserHive() {
        long hiveId = 4L;
        HiveEntity hiveEntity = HiveEntity.builder().id(hiveId).queen(QueenEntity.builder().build()).owner(testData.getUser2()).build();
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(hiveEntity));
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any())).thenReturn(false);

        ResponseEntity<byte[]> response = queenServiceImpl.getQueenImage(hiveId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository).findById(hiveId);
        verify(friendshipUtils).isFriendshipStatus(user.getId(), testData.getUser2().getId(), FriendshipEnums.EStatus.FRIEND);
        verifyNoMoreInteractions(hiveRepository, friendshipUtils);
    }

    @Test
    void testGetQueenImage_NoMyImageUploaded() {
        long hiveId = 9L;
        HiveEntity hiveEntity = HiveEntity.builder().id(hiveId).queen(QueenEntity.builder().build()).owner(user).build();

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(hiveEntity));
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any())).thenReturn(false);

        ResponseEntity<byte[]> response = queenServiceImpl.getQueenImage(hiveId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(hiveRepository).findById(hiveId);
        verifyNoMoreInteractions(hiveRepository, friendshipUtils);
    }

    @Test
    void testGetQueenImage_NoFriendImageUploaded() {
        long hiveId = 9L;
        HiveEntity hiveEntity = HiveEntity.builder().id(hiveId).queen(QueenEntity.builder().build()).owner(testData.getUser2()).build();

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(hiveEntity));
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any())).thenReturn(true);

        ResponseEntity<byte[]> response = queenServiceImpl.getQueenImage(hiveId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(hiveRepository).findById(hiveId);
        verify(friendshipUtils).isFriendshipStatus(eq(user.getId()), eq(testData.getUser2().getId()), any());
        verifyNoMoreInteractions(hiveRepository, friendshipUtils);
    }

    @Test
    void testGetQueenImage_MyFriendImageSuccess() {
        long hiveId = 1L;
        HiveEntity hiveEntity = HiveEntity.builder().id(hiveId).queen(QueenEntity.builder().image(new byte[]{1, 2, 3}).build()).owner(testData.getUser2()).build();

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(hiveEntity));
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any())).thenReturn(true);

        ResponseEntity<byte[]> response = queenServiceImpl.getQueenImage(hiveId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ImageUtil.decompressImage(hiveEntity.getQueen().getImage()).length, response.getBody().length);
        assertTrue(Arrays.equals(ImageUtil.decompressImage(hiveEntity.getQueen().getImage()), response.getBody()));
        verify(hiveRepository).findById(hiveId);
        verify(friendshipUtils).isFriendshipStatus(eq(user.getId()), eq(testData.getUser2().getId()), any());
        verifyNoMoreInteractions(hiveRepository, friendshipUtils);
    }

    @Test
    void testGetQueenImage_MyImageSuccess() {
        long hiveId = 1L;
        HiveEntity hiveEntity = HiveEntity.builder().id(hiveId).queen(QueenEntity.builder().image(new byte[]{1, 2, 3}).build()).owner(user).build();

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(hiveEntity));
        when(friendshipUtils.isFriendshipStatus(anyLong(), anyLong(), any())).thenReturn(false);

        ResponseEntity<byte[]> response = queenServiceImpl.getQueenImage(hiveId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ImageUtil.decompressImage(hiveEntity.getQueen().getImage()).length, response.getBody().length);
        assertTrue(Arrays.equals(ImageUtil.decompressImage(hiveEntity.getQueen().getImage()), response.getBody()));
        verify(hiveRepository).findById(hiveId);
        verifyNoMoreInteractions(hiveRepository, friendshipUtils);
    }
}

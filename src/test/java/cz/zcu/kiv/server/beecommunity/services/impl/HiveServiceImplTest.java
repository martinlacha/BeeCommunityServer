package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.SensorDataDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.ApiaryEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.SensorsDataEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.ApiaryRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.HiveRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.SensorsDataRepository;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HiveServiceImplTest {

    @Mock
    private ApiaryRepository apiaryRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private ObjectMapper modelMapper;

    @InjectMocks
    private HiveServiceImpl hiveService;

    @Mock
    private FriendshipUtils friendshipUtils;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private SensorsDataRepository sensorsDataRepository;

    private UserEntity user;

    private ApiaryEntity apiary;

    private HiveEntity hive;

    private HiveDto hiveDto;

    private final TestData testData = new TestData();

    private String structure;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        user = testData.getUser1();
        apiary = testData.getApiaryEntity1();
        hive = testData.getHives().get(0);
        hiveDto = testData.getHivesDto().get(0);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        structure = "Testing structure";
    }

    @Test
    void testGetHives_ApiaryNotFound() {
        when(apiaryRepository.findById(hive.getApiary().getId())).thenReturn(Optional.empty());

        ResponseEntity<List<HiveDto>> response = hiveService.getHives(hive.getApiary().getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(apiary.getId());
        verifyNoInteractions(hiveRepository, modelMapper);
    }

    @Test
    void testGetHives_NotApiaryOwnerOrFriend() {
        apiary.setOwner(testData.getUser2());
        when(apiaryRepository.findById(apiary.getId())).thenReturn(Optional.of(apiary));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(apiary.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        ResponseEntity<List<HiveDto>> response = hiveService.getHives(apiary.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(apiary.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), apiary.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
        verifyNoInteractions(hiveRepository, modelMapper);
    }

    @Test
    void testGetHives_IsFriend_Success() {
        apiary.setOwner(testData.getUser3());
        when(apiaryRepository.findById(apiary.getId())).thenReturn(Optional.of(apiary));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(apiary.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);
        when(hiveRepository.findByApiaryIdOrderById(eq(apiary.getId()))).thenReturn(testData.getHives());
        when(modelMapper.convertHiveEntityList(eq(testData.getHives()))).thenReturn(testData.getHivesDto());

        ResponseEntity<List<HiveDto>> response = hiveService.getHives(apiary.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(apiary.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), apiary.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
        verify(hiveRepository, times(1)).findByApiaryIdOrderById(apiary.getId());
        verify(modelMapper, times(1)).convertHiveEntityList(testData.getHives());
    }

    @Test
    void testCreateHive_ApiaryNotFound() {
        when(apiaryRepository.findById(hiveDto.getApiaryId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = hiveService.createHive(hiveDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(hiveDto.getApiaryId());
        verifyNoInteractions(friendshipUtils, modelMapper, hiveRepository);
    }

    @Test
    void testCreateHive_NotOwnerOrFriend() {
        apiary.setOwner(testData.getUser2());
        when(apiaryRepository.findById(eq(hiveDto.getApiaryId()))).thenReturn(Optional.of(apiary));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(apiary.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        ResponseEntity<Void> response = hiveService.createHive(hiveDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(apiaryRepository, times(1)).findById(apiary.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), apiary.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
        verifyNoInteractions(hiveRepository, modelMapper);
    }

    @Test
    void testCreateHive_IsOwner_Success() {
        apiary.setOwner(user);
        when(apiaryRepository.findById(hiveDto.getApiaryId())).thenReturn(Optional.of(apiary));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(apiary.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);
        when(modelMapper.convertHiveDto(eq(hiveDto))).thenReturn(hive);

        ResponseEntity<Void> response = hiveService.createHive(hiveDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(modelMapper, times(1)).convertHiveDto(hiveDto);
        verify(apiaryRepository, times(1)).findById(apiary.getId());
        verify(hiveRepository, times(1)).saveAndFlush(hive);
    }

    @Test
    void testCreateHive_IsFriend_Success() {
        apiary.setOwner(testData.getUser3());
        when(apiaryRepository.findById(hiveDto.getApiaryId())).thenReturn(Optional.of(apiary));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(apiary.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);
        when(modelMapper.convertHiveDto(eq(hiveDto))).thenReturn(hive);

        ResponseEntity<Void> response = hiveService.createHive(hiveDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(modelMapper, times(1)).convertHiveDto(hiveDto);
        verify(apiaryRepository, times(1)).findById(apiary.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), apiary.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
        verify(hiveRepository, times(1)).saveAndFlush(hive);
    }

    @Test
    void testUpdateHive_HiveNotFound() {
        when(hiveRepository.findById(hiveDto.getId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = hiveService.updateHive(hiveDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hiveDto.getId());
        verify(hiveRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateHive_NotOwner_BadRequest() {
        hive.setOwner(testData.getUser3());
        when(hiveRepository.findById(hiveDto.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<Void> response = hiveService.updateHive(hiveDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hiveDto.getId());
        verify(hiveRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateHive_ApiaryHiveNotEqualToHiveDtoApiaryId_Conflict() {
        hive.setOwner(user);
        hiveDto.setApiaryId(8L);
        when(hiveRepository.findById(hiveDto.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<Void> response = hiveService.updateHive(hiveDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(hiveRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateHive_Success() {
        hive.setOwner(user);
        when(hiveRepository.findById(hiveDto.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<Void> response = hiveService.updateHive(hiveDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testDeleteHive_NotFoundHive() {
        hive.setOwner(user);
        when(hiveRepository.findById(hiveDto.getId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = hiveService.deleteHive(hiveDto.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(any());
        verifyNoMoreInteractions(hiveRepository);
    }

    @Test
    void testDeleteHive_NotOwner_BadRequest() {
        hive.setOwner(testData.getUser3());
        when(hiveRepository.findById(hiveDto.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<Void> response = hiveService.deleteHive(hive.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoMoreInteractions(hiveRepository);
    }

    @Test
    void testDeleteHive_Success() {
        hive.setOwner(user);
        when(hiveRepository.findById(hiveDto.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<Void> response = hiveService.deleteHive(hiveDto.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(hiveRepository, times(1)).deleteById(any());
        verify(hiveRepository, times(1)).flush();
    }

    @Test
    void testGetHiveDetail_HiveNotFound() {
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.empty());

        ResponseEntity<HiveDto> response = hiveService.getHiveDetail(hive.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoInteractions(friendshipUtils, modelMapper);
    }

    @Test
    void testGetHiveDetail_NotOwnerOrFriend() {
        hive.setOwner(testData.getUser2());
        when(hiveRepository.findById(eq(hive.getId()))).thenReturn(Optional.of(hive));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        ResponseEntity<HiveDto> response = hiveService.getHiveDetail(hive.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), hive.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testGetHiveDetail_IsFriend_Success() {
        hive.setOwner(testData.getUser2());
        when(hiveRepository.findById(eq(hive.getId()))).thenReturn(Optional.of(hive));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);

        ResponseEntity<HiveDto> response = hiveService.getHiveDetail(hive.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(modelMapper, times(1)).convertHiveEntity(hive);
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), hive.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
    }

    @Test
    void testGetHiveDetail_IsOwner_Success() {
        hive.setOwner(user);
        when(hiveRepository.findById(eq(hive.getId()))).thenReturn(Optional.of(hive));

        ResponseEntity<HiveDto> response = hiveService.getHiveDetail(hive.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(modelMapper, times(1)).convertHiveEntity(hive);
    }

    @Test
    void testGetHiveImage_HiveNotFound() {
        when(hiveRepository.findById(eq(hive.getId()))).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = hiveService.getHiveImage(hive.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoInteractions(friendshipUtils);
    }

    @Test
    void testGetHiveImage_NotOwnerOrFriend() {
        hive.setOwner(testData.getUser3());
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        ResponseEntity<byte[]> response = hiveService.getHiveImage(hive.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), hive.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
    }

    @Test
    void testGetHiveImage_IsFriend_Success() {
        try (MockedStatic<ImageUtil> utilities = Mockito.mockStatic(ImageUtil.class)) {
            String myHiveImage = "This is example of my hive image data";
            hive.setOwner(testData.getUser3());
            hive.setImage(myHiveImage.getBytes());
            when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
            when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);
            utilities.when(() -> ImageUtil.decompressImage(myHiveImage.getBytes())).thenReturn(myHiveImage.getBytes());


            ResponseEntity<byte[]> response = hiveService.getHiveImage(hive.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertArrayEquals(myHiveImage.getBytes(), response.getBody());
            verify(hiveRepository, times(1)).findById(hive.getId());
            verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), hive.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
        }
    }

    @Test
    void testGetHiveImage_IsOwner_Success() {
        try (MockedStatic<ImageUtil> utilities = Mockito.mockStatic(ImageUtil.class)) {
            String myHiveImage = "This is example of my hive image data";
            hive.setOwner(user);
            hive.setImage(myHiveImage.getBytes());
            when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
            utilities.when(() -> ImageUtil.decompressImage(myHiveImage.getBytes())).thenReturn(myHiveImage.getBytes());


            ResponseEntity<byte[]> response = hiveService.getHiveImage(hive.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertArrayEquals(myHiveImage.getBytes(), response.getBody());
            verify(hiveRepository, times(1)).findById(hive.getId());
        }
    }

    @Test
    void testGetHiveStructure_HiveNotFound() {
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.empty());

        ResponseEntity<String> response = hiveService.getHiveStructure(hive.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoInteractions(friendshipUtils);
    }

    @Test
    void testGetHiveStructure_NotOwnerOrFriend() {
        hive.setOwner(testData.getUser3());
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        ResponseEntity<String> response = hiveService.getHiveStructure(hive.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), hive.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
    }

    @Test
    void testGetHiveStructure_IsFriend_Success() {
        hive.setOwner(testData.getUser3());
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);

        ResponseEntity<String> response = hiveService.getHiveStructure(hive.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(user.getId(), hive.getOwner().getId(), FriendshipEnums.EStatus.FRIEND);
    }

    @Test
    void testGetHiveStructure_IsOwner_Success() {
        hive.setOwner(user);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<String> response = hiveService.getHiveStructure(hive.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoInteractions(friendshipUtils);
    }

    @Test
    void testCreateHiveStructure_HiveNotFound() {
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = hiveService.createHiveStructure(hive.getId(), structure);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoMoreInteractions(hiveRepository);
    }

    @Test
    void testCreateHiveStructure_NotOwnerOrFriend_BadRequest() {
        hive.setOwner(testData.getUser2());
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<Void> response = hiveService.createHiveStructure(hive.getId(), structure);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoMoreInteractions(hiveRepository);
    }

    @Test
    void testCreateHiveStructure_Success() {
        hive.setOwner(user);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));

        ResponseEntity<Void> response = hiveService.createHiveStructure(hive.getId(), structure);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(hiveRepository, times(1)).saveAndFlush(hive);
    }

    @Test
    void testUploadSensorsData_NotFound() {
        hive.setOwner(user);
        var sensorsData = SensorDataDto.builder().hive(hive.getId()).hiveName(hive.getName()).build();
        when(hiveRepository.findByName(hive.getName())).thenReturn(Optional.empty());

        var response = hiveService.uploadSensorsData(sensorsData);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findByName(eq(hive.getName()));
    }

    @Test
    void testUploadSensorsData_Success() {
        hive.setOwner(user);
        var sensorsData = SensorDataDto.builder().hive(hive.getId()).hiveName(hive.getName()).build();
        when(hiveRepository.findByName(hive.getName())).thenReturn(Optional.of(hive));
        when(modelMapper.convertSensorsDataDto(any())).thenReturn(new SensorsDataEntity());

        var response = hiveService.uploadSensorsData(sensorsData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findByName(hive.getName());
        verify(sensorsDataRepository, times(1)).save(any());
    }

    @Test
    void testGetSensorsData_Success() {
        hive.setOwner(user);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));

        var response = hiveService.getHiveSensorsData(hive.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
    }

    @Test
    void testGetSensorsData_NotFound() {
        hive.setOwner(user);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.empty());

        var response = hiveService.getHiveSensorsData(hive.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
    }
}

package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.InspectionDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.HiveEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.InspectionEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.HiveRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.InspectionRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InspectionServiceImplTest {

    @Mock
    private InspectionRepository inspectionRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private FriendshipUtils friendshipUtils;

    @Mock
    private ObjectMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InspectionServiceImpl inspectionService;

    private UserEntity user;

    private HiveEntity hive;

    private InspectionDetailDto detail;

    private InspectionEntity inspection;

    private final TestData testData = new TestData();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        user = testData.getUser1();
        hive = testData.getHives().get(0);
        when(authentication.getPrincipal()).thenReturn(user);
        detail = testData.getInspectionDetailDtoList().get(0);
        inspection = testData.getInspectionEntities().get(0);
    }

    @Test
    void testGetInspections_HiveNotFound() {
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.empty());
        ResponseEntity<List<InspectionDto>> response = inspectionService.getInspections(hive.getId());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verify(inspectionRepository, never()).findByHiveIdOrderByInspectionDate(hive.getId());
    }

    @Test
    void testGetInspections_NotHiveOwnerOrFriend() {
        // Set another user to the hive
        hive.setOwner(testData.getUser2());
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        ResponseEntity<List<InspectionDto>> response = inspectionService.getInspections(hive.getId());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(hiveRepository, times(1)).findById(hive.getId());
        verifyNoInteractions(inspectionRepository);
    }



    @Test
    void testGetInspections_IsFriend_Success() {
        hive.setOwner(testData.getUser2());
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);

        List<InspectionEntity> inspectionEntities = Collections.singletonList(new InspectionEntity());
        when(inspectionRepository.findByHiveIdOrderById(hive.getId())).thenReturn(inspectionEntities);

        List<InspectionDto> expectedDtos = Collections.singletonList(new InspectionDto());
        when(modelMapper.convertInspectionEntityList(inspectionEntities)).thenReturn(expectedDtos);

        ResponseEntity<List<InspectionDto>> response = inspectionService.getInspections(hive.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetInspections_IsOwner_Success() {
        hive.setOwner(user);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(hive.getOwner().getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        List<InspectionEntity> inspectionEntities = Collections.singletonList(new InspectionEntity());
        when(inspectionRepository.findByHiveIdOrderById(hive.getId())).thenReturn(inspectionEntities);

        List<InspectionDto> expectedDtos = Collections.singletonList(new InspectionDto());
        when(modelMapper.convertInspectionEntityList(inspectionEntities)).thenReturn(expectedDtos);

        ResponseEntity<List<InspectionDto>> response = inspectionService.getInspections(hive.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateInspection_HiveNotFound() {
        InspectionDetailDto detail = testData.getInspectionDetailDtoList().get(0);
        when(hiveRepository.findById(detail.getHiveId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = inspectionService.createInspection(detail);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(inspectionRepository);
    }

    @Test
    void testCreateInspection_NotOwnerOfHive() {
        var entity = testData.getInspectionEntities().get(0);
        hive.setOwner(testData.getUser2());
        InspectionDetailDto detail = testData.getInspectionDetailDtoList().get(0);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(modelMapper.convertInspectionDto(detail)).thenReturn(entity);

        ResponseEntity<Void> response = inspectionService.createInspection(detail);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(inspectionRepository);
    }

    @Test
    void testCreateInspection_Success() {
        var entity = testData.getInspectionEntities().get(0);
        hive.setOwner(user);
        InspectionDetailDto detail = testData.getInspectionDetailDtoList().get(0);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(modelMapper.convertInspectionDto(detail)).thenReturn(entity);

        ResponseEntity<Void> response = inspectionService.createInspection(detail);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(inspectionRepository, times(1)).saveAndFlush(any());
    }


    @Test
    void testUpdateInspection_HiveNotFound() {
        when(hiveRepository.findById(detail.getHiveId())).thenReturn(Optional.empty());
        when(modelMapper.convertInspectionDto(detail)).thenReturn(InspectionEntity.builder().id(detail.getId()).build());

        ResponseEntity<Void> response = inspectionService.updateInspection(detail);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(modelMapper, times(1)).convertInspectionDto(detail);
        verify(inspectionRepository, times(1)).findById(detail.getId());
        verify(inspectionRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateInspection_InspectionNotFound() {
        when(hiveRepository.findById(detail.getHiveId())).thenReturn(Optional.of(hive));
        when(inspectionRepository.findById(detail.getId())).thenReturn(Optional.empty());
        when(modelMapper.convertInspectionDto(detail)).thenReturn(InspectionEntity.builder().id(detail.getId()).build());

        ResponseEntity<Void> response = inspectionService.updateInspection(detail);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(modelMapper, times(1)).convertInspectionDto(detail);
        verify(hiveRepository, times(1)).findById(detail.getHiveId());
        verify(inspectionRepository, times(1)).findById(detail.getId());
        verify(inspectionRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateInspection_NotHiveOwner() {
        hive.setOwner(testData.getUser2());
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(inspectionRepository.findById(detail.getId())).thenReturn(Optional.of(InspectionEntity.builder().owner(user).build()));
        when(modelMapper.convertInspectionDto(detail)).thenReturn(InspectionEntity.builder().id(detail.getId()).build());

        ResponseEntity<Void> response = inspectionService.updateInspection(detail);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(modelMapper, times(1)).convertInspectionDto(detail);
        verify(hiveRepository, times(1)).findById(detail.getHiveId());
        verify(inspectionRepository, times(1)).findById(detail.getId());
        verify(inspectionRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateInspection_NotInspectionCreator() {
        hive.setOwner(user);
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(inspectionRepository.findById(detail.getId())).thenReturn(Optional.of(InspectionEntity.builder().owner(testData.getUser2()).build()));
        when(modelMapper.convertInspectionDto(detail)).thenReturn(InspectionEntity.builder().id(detail.getId()).build());

        ResponseEntity<Void> response = inspectionService.updateInspection(detail);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(modelMapper, times(1)).convertInspectionDto(detail);
        verify(hiveRepository, times(1)).findById(detail.getHiveId());
        verify(inspectionRepository, times(1)).findById(detail.getId());
        verify(inspectionRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateInspection_Success() {
        hive.setOwner(user);
        when(modelMapper.convertInspectionDto(detail)).thenReturn(InspectionEntity.builder().id(detail.getId()).build());
        when(hiveRepository.findById(hive.getId())).thenReturn(Optional.of(hive));
        when(inspectionRepository.findById(detail.getId())).thenReturn(Optional.of(InspectionEntity.builder().owner(user).build()));
        when(modelMapper.convertInspectionDto(detail)).thenReturn(InspectionEntity.builder().id(detail.getId()).build());


        ResponseEntity<Void> response = inspectionService.updateInspection(detail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(inspectionRepository, times(1)).saveAndFlush(any());
        verify(modelMapper, times(1)).convertInspectionDto(detail);
        verify(hiveRepository, times(1)).findById(detail.getHiveId());
        verify(inspectionRepository, times(1)).findById(detail.getId());
    }

    @Test
    void testDeleteInspection_InspectionNotFound() {
        when(inspectionRepository.findById(detail.getId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = inspectionService.deleteInspection(detail.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(inspectionRepository, never()).delete(any());
        verify(inspectionRepository, never()).flush();
    }

    @Test
    void testDeleteInspection_NotCreatorOrFriend() {
        inspection.setOwner(testData.getUser2());
        when(inspectionRepository.findById(inspection.getId())).thenReturn(Optional.of(inspection));

        ResponseEntity<Void> response = inspectionService.deleteInspection(inspection.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(inspectionRepository, times(1)).findById(inspection.getId());
        verify(inspectionRepository, never()).delete(any());
        verify(inspectionRepository, never()).flush();
    }

    @Test
    void testDeleteInspection_Success() {
        when(inspectionRepository.findById(inspection.getId())).thenReturn(Optional.of(inspection));

        ResponseEntity<Void> response = inspectionService.deleteInspection(inspection.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(inspectionRepository, times(1)).findById(inspection.getId());
        verify(inspectionRepository, times(1)).delete(inspection);
        verify(inspectionRepository, times(1)).flush();
    }

    @Test
    void testGetInspectionDetail_InspectionNotFound() {
        when(inspectionRepository.findById(inspection.getId())).thenReturn(Optional.empty());

        ResponseEntity<InspectionDetailDto> response = inspectionService.getInspectionDetail(inspection.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(inspectionRepository, times(1)).findById(inspection.getId());
    }

    @Test
    void testGetInspectionDetail_NotCreatorOrFriend() {
        inspection.setOwner(testData.getUser3());
        when(inspectionRepository.findById(inspection.getId())).thenReturn(Optional.of(inspection));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(testData.getUser3().getId()), any())).thenReturn(false);

        ResponseEntity<InspectionDetailDto> response = inspectionService.getInspectionDetail(inspection.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(inspectionRepository, times(1)).findById(inspection.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(eq(user.getId()), eq(testData.getUser3().getId()), any());
        verify(modelMapper, never()).convertInspectionEntity(inspection);
    }

    @Test
    void testGetInspectionDetail_IsFriend_Success() {
        inspection.setOwner(testData.getUser3());
        when(inspectionRepository.findById(inspection.getId())).thenReturn(Optional.of(inspection));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(testData.getUser3().getId()), any())).thenReturn(true);
        when(modelMapper.convertInspectionEntity(inspection)).thenReturn(detail);

        ResponseEntity<InspectionDetailDto> response = inspectionService.getInspectionDetail(inspection.getId());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(inspectionRepository, times(1)).findById(inspection.getId());
        verify(friendshipUtils, times(1)).isFriendshipStatus(eq(user.getId()), eq(testData.getUser3().getId()), any());
        verify(modelMapper, times(1)).convertInspectionEntity(inspection);
    }

    @Test
    void testGetInspectionDetail_IsCreator_Success() {
        when(inspectionRepository.findById(inspection.getId())).thenReturn(Optional.of(inspection));
        when(modelMapper.convertInspectionEntity(inspection)).thenReturn(detail);

        ResponseEntity<InspectionDetailDto> response = inspectionService.getInspectionDetail(inspection.getId());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(detail, response.getBody());
        verify(inspectionRepository, times(1)).findById(inspection.getId());
        verify(modelMapper, times(1)).convertInspectionEntity(inspection);
    }

    @Test
    void testGetImageByType_InspectionNotFound() {
        when(inspectionRepository.findByOwnerIdAndId(user.getId(), inspection.getId())).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = inspectionService.getImageByType(inspection.getId(), InspectionEnums.EImageType.INSPECTION);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(inspectionRepository, times(1)).findByOwnerIdAndId(user.getId(), inspection.getId());
    }
    @Test
    void testGetImageByType_ImageTypeFound_ImageNull() {
        when(inspectionRepository.findByOwnerIdAndId(1L, inspection.getId())).thenReturn(Optional.of(inspection));

        ResponseEntity<byte[]> response = inspectionService.getImageByType(inspection.getId(), InspectionEnums.EImageType.INSPECTION);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(inspectionRepository, times(1)).findByOwnerIdAndId(user.getId(), inspection.getId());
    }

    @Test
    void testGetImageByType_ImageTypeNotFound() {
        when(inspectionRepository.findByOwnerIdAndId(user.getId(), inspection.getId())).thenReturn(Optional.of(inspection));

        ResponseEntity<byte[]> response =
                inspectionService.getImageByType(inspection.getId(), InspectionEnums.EImageType.INSPECTION);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(inspectionRepository, times(1)).findByOwnerIdAndId(user.getId(), inspection.getId());

    }

    @Test
    void testGetImageByType_ImageFound() {
        try (MockedStatic<ImageUtil> utilities = Mockito.mockStatic(ImageUtil.class)) {
            String imageData = "This is test data of image";
            inspection.setInspectionImage(imageData.getBytes());
            when(inspectionRepository.findByOwnerIdAndId(user.getId(), inspection.getId())).thenReturn(Optional.of(inspection));
            utilities.when(() -> ImageUtil.decompressImage(imageData.getBytes())).thenReturn(imageData.getBytes());
            ResponseEntity<byte[]> response = inspectionService.getImageByType(inspection.getId(), InspectionEnums.EImageType.INSPECTION);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertArrayEquals(imageData.getBytes(), response.getBody());
            verify(inspectionRepository, times(1)).findByOwnerIdAndId(user.getId(), inspection.getId());
        }
    }
}

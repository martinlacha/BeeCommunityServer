package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.enums.*;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.GetUpdateUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.CommunityPostEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.FriendshipEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserInfoEntity;
import cz.zcu.kiv.server.beecommunity.testData.ObjectMapperTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ObjectMapperTest {
    @Autowired
    private ObjectMapper objectMapper;

    ObjectMapperTestData testData = new ObjectMapperTestData();

    @Test
    void testConvertToNewUserEntity() {
        UserEntity actualUserEntity = objectMapper.convertToNewUserEntity(testData.getNewUserDto());
        assertEquals(testData.getExpectedUserEntity().getEmail(), actualUserEntity.getEmail());
        assertEquals(testData.getExpectedUserEntity().getPassword(), actualUserEntity.getPassword());
    }

    @Test
    void testConvertToUserInfoEntity() {
        // Get converted entity from dto
        UserInfoEntity actualUserInfoEntity = objectMapper.convertToUserInfoEntity(testData.getNewUserInfoDto1());
        // Assert actual and expected
        var expectedUserInfoEntity = testData.getExpectedUserInfoEntity();
        assertEquals(expectedUserInfoEntity.getName(), actualUserInfoEntity.getName());
        assertEquals(expectedUserInfoEntity.getSurname(), actualUserInfoEntity.getSurname());
        assertEquals(expectedUserInfoEntity.getDateOfBirth(), actualUserInfoEntity.getDateOfBirth());
        assertEquals(expectedUserInfoEntity.getExperience(), actualUserInfoEntity.getExperience());
        assertEquals(expectedUserInfoEntity.getAddress().getState(), actualUserInfoEntity.getAddress().getState());
        assertEquals(expectedUserInfoEntity.getAddress().getCountry(), actualUserInfoEntity.getAddress().getCountry());
        assertEquals(expectedUserInfoEntity.getAddress().getTown(), actualUserInfoEntity.getAddress().getTown());
        assertEquals(expectedUserInfoEntity.getAddress().getStreet(), actualUserInfoEntity.getAddress().getStreet());
        assertEquals(expectedUserInfoEntity.getAddress().getNumber(), actualUserInfoEntity.getAddress().getNumber());
    }

    @Test
    void testConvertUserInfoDto() {
        var expectedUserInfoEntity = testData.getExpectedUserInfoEntity();
        GetUpdateUserInfoDto infoDto = objectMapper.convertUserInfoDto(expectedUserInfoEntity);
        assertEquals("John", infoDto.getName());
        assertEquals("Doe", infoDto.getSurname());
        assertEquals(UserEnums.EExperience.BEGINNER, infoDto.getExperience());
        assertEquals("1990-01-01", infoDto.getDateOfBirth());
        assertEquals("US", infoDto.getCountry());
        assertEquals("NY", infoDto.getState());
        assertEquals("New York", infoDto.getTown());
        assertEquals("Main St", infoDto.getStreet());
        assertEquals(456, infoDto.getNumber());
    }

    @Test
    void testConvertListUserEntity() {
        var user1 = testData.getUser1();
        var user2 = testData.getUser2();
        List<UserEntity> userEntityList = List.of(user1, user2);
        List<FoundUserDto> expectedDtoList = new ArrayList<>();
        expectedDtoList.add(new FoundUserDto("john@example.com", "John", "Doe", "NY", "US", "New York"));
        expectedDtoList.add(new FoundUserDto("jane@example.com", "Jane", "Doe", "CA", "US", "Los Angeles"));
        List<FoundUserDto> actualDtoList = objectMapper.convertListUserEntity(userEntityList);
        assertEquals(expectedDtoList.size(), actualDtoList.size());
        for (int i = 0; i < expectedDtoList.size(); i++) {
            assertEquals(expectedDtoList.get(i), actualDtoList.get(i));
        }
    }

    @Test
    void testConvertListFriendship() {
        var user1 = testData.getUser1();
        var user2 = testData.getUser2();
        var user3 = testData.getUser3();
        FriendshipEntity friendship1 = FriendshipEntity
                .builder()
                .sender(user1)
                .receiver(user2)
                .status(FriendshipEnums.EStatus.FRIEND)
                .build();
        FriendshipEntity friendship2 = FriendshipEntity
                .builder()
                .sender(user3)
                .receiver(user1)
                .status(FriendshipEnums.EStatus.FRIEND)
                .build();

        List<FriendshipEntity> friendshipList = List.of(friendship1, friendship2);

        List<FoundUserDto> expectedDtoList = new ArrayList<>();
        expectedDtoList.add(new FoundUserDto("jane@example.com", "Jane", "Doe", "CA", "US", "Los Angeles"));
        expectedDtoList.add(new FoundUserDto("martin@example.com", "Martin", "Lacha", "CZ", "CZ", "Chlumany"));

        List<FoundUserDto> actualDtoList = objectMapper.convertListFriendship(friendshipList, user1.getId());

        assertEquals(expectedDtoList.size(), actualDtoList.size());
        for (int i = 0; i < expectedDtoList.size(); i++) {
            assertEquals(expectedDtoList.get(i), actualDtoList.get(i));
        }
    }

    @Test
    void testConvertPostListToDtoList() {
        var post1 = testData.getPost1();
        var post2 = testData.getPost2();
        var post3 = testData.getPost3();
        List<CommunityPostEntity> postList = new ArrayList<>();
        postList.add(post1);
        postList.add(post2);
        postList.add(post3);

        List<CommunityPostDto> expectedDtoList = new ArrayList<>();
        expectedDtoList.add(new CommunityPostDto(1L, "John Doe", "Title 1", null, null, null, CommunityEnums.EType.INFO, LocalDate.now().toString(), null));
        expectedDtoList.add(new CommunityPostDto(2L, "Jane Doe", "Title 2", null, null, null, CommunityEnums.EType.WARN, LocalDate.now().toString(), null));
        expectedDtoList.add(new CommunityPostDto(3L, "Martin Lacha", "Title 3", null, null, null, CommunityEnums.EType.NONE, LocalDate.now().atStartOfDay().toLocalDate().toString(), null));

        List<CommunityPostDto> actualDtoList = objectMapper.convertPostListToDtoList(postList);

        assertEquals(expectedDtoList.size(), actualDtoList.size());
        for (int i = 0; i < expectedDtoList.size(); i++) {
            assertEquals(expectedDtoList.get(i), actualDtoList.get(i));
        }
    }

    @Test
    void testConvertPostEntityToDto() {
        var post1 = testData.getPost1();
        var post2 = testData.getPost2();
        var post3 = testData.getPost3();
        var post4 = testData.getPost4();
        post3.getComments().forEach(comment -> comment.setPost(post3));
        CommunityPostDto dto1 = objectMapper.convertPostEntityToDto(post1);
        CommunityPostDto dto2 = objectMapper.convertPostEntityToDto(post2);
        CommunityPostDto dto3 = objectMapper.convertPostEntityToDto(post3);
        CommunityPostDto dto4 = objectMapper.convertPostEntityToDto(post4);

        assertEquals(1L, dto1.getId());
        assertEquals("John Doe", dto1.getAuthor());
        assertEquals("Title 1", dto1.getTitle());
        assertEquals("Post 1", dto1.getPost());
        assertEquals(CommunityEnums.EAccess.PUBLIC, dto1.getAccess());
        assertEquals(LocalDate.now().toString(), dto1.getDate());
        assertEquals(CommunityEnums.EType.INFO, dto1.getType());

        assertEquals(2L, dto2.getId());
        assertEquals("Jane Doe", dto2.getAuthor());
        assertEquals("Title 2", dto2.getTitle());
        assertEquals("Post 2", dto2.getPost());
        assertEquals(CommunityEnums.EAccess.PRIVATE, dto2.getAccess());
        assertEquals(LocalDate.now().toString(), dto2.getDate());
        assertEquals(CommunityEnums.EType.WARN, dto2.getType());

        assertEquals(3L, dto3.getId());
        assertEquals("Martin Lacha", dto3.getAuthor());
        assertEquals("Title 3", dto3.getTitle());
        assertEquals("Post 3", dto3.getPost());
        assertEquals(CommunityEnums.EAccess.PUBLIC, dto3.getAccess());
        assertEquals(LocalDate.now().toString(), dto3.getDate());
        assertEquals(CommunityEnums.EType.NONE, dto3.getType());
        assertEquals(post3.getComments().size(), dto3.getComments().size());
        assertEquals("First comment", post3.getComments().get(0).getComment());
        assertEquals("John Doe", post3.getComments().get(0).getAuthor().getFullName());
        assertEquals("Second comment", post3.getComments().get(1).getComment());
        assertEquals("Jane Doe", post3.getComments().get(1).getAuthor().getFullName());

        assertEquals(4L, dto4.getId());
        assertEquals("John Doe", dto4.getAuthor());
        assertEquals("Title 4", dto4.getTitle());
        assertEquals("Post 4", dto4.getPost());
        assertEquals(CommunityEnums.EAccess.PRIVATE, dto4.getAccess());
        assertEquals(LocalDate.now().toString(), dto4.getDate());
        assertEquals(CommunityEnums.EType.EMERGENCY, dto4.getType());
    }

    @Test
    void testConvertCommentToDto() {
        var post3 = testData.getPost3();
        post3.getComments().forEach(comment -> comment.setPost(post3));
        PostCommentDto commentDto1 = objectMapper.convertCommentToDto(post3.getComments().get(0));
        PostCommentDto commentDto2 = objectMapper.convertCommentToDto(post3.getComments().get(1));

        assertEquals(1L, commentDto1.getId().longValue());
        assertEquals("John Doe", commentDto1.getAuthor());
        assertEquals("First comment", commentDto1.getComment());
        assertEquals(3L, commentDto2.getPostId().longValue());
        assertEquals(LocalDate.now().toString(), commentDto1.getDate());

        assertEquals(2L, commentDto2.getId().longValue());
        assertEquals("Jane Doe", commentDto2.getAuthor());
        assertEquals("Second comment", commentDto2.getComment());
        assertEquals(3L, commentDto2.getPostId().longValue());
        assertEquals(LocalDate.now().toString(), commentDto2.getDate());
    }


    @Test
    void testConvertPostDtoToEntity() {
        var post = testData.getPostDto();
        // Call the convertPostDtoToEntity method
        CommunityPostEntity postEntity = objectMapper.convertPostDtoToEntity(post);

        // Verify that the entity has been created correctly
        assertEquals("Title 1", postEntity.getTitle());
        assertEquals("Post 1", postEntity.getPost());
        assertNull(postEntity.getImage());
        assertEquals(CommunityEnums.EAccess.PUBLIC, postEntity.getAccess());
        assertEquals(CommunityEnums.EType.INFO, postEntity.getType());
    }

    @Test
    void testConvertNewsEntityToDto() {
        var newsDetail = objectMapper.convertNewsEntityToDto(testData.getNews1());
        assertEquals(testData.getNewsDetailDto1(), newsDetail);
    }

    @Test
    void testConvertNewsDtoToEntity() {
        var news1 = testData.getNews1();
        var newsEntity = objectMapper.convertNewsDtoToEntity(testData.getNewsDetailDto1());
        assertEquals(newsEntity.getTitle(), news1.getTitle());
        assertEquals(newsEntity.getArticle(), news1.getArticle());
        assertNull(newsEntity.getFirstImage());
        assertNull(newsEntity.getSecondImage());
        assertNull(newsEntity.getTitleImage());
    }

    @Test
    void testConvertNewsList() {
        var list = List.of(testData.getNews2(), testData.getNews1());
        var converted = objectMapper.convertNewsList(list);
        var newsDetailDto1 = testData.getNewsDetailDto1();
        var newsDetailDto2 = testData.getNewsDetailDto2();

        assertEquals(2, converted.size());

        assertEquals(newsDetailDto1.getId(), converted.get(0).getId());
        assertEquals(newsDetailDto1.getTitle(), converted.get(0).getTitle());
        assertEquals(newsDetailDto1.getArticle(), converted.get(0).getArticle());
        assertEquals(newsDetailDto1.getAuthor(), converted.get(0).getAuthor());
        assertEquals(newsDetailDto1.getDate(), converted.get(0).getDate());

        assertEquals(newsDetailDto2.getId(), converted.get(1).getId());
        assertEquals(newsDetailDto2.getTitle(), converted.get(1).getTitle());
        assertEquals(newsDetailDto2.getArticle(), converted.get(1).getArticle());
        assertEquals(newsDetailDto2.getAuthor(), converted.get(1).getAuthor());
        assertEquals(newsDetailDto2.getDate(), converted.get(1).getDate());
    }

    @Test
    void testConvertApiaryDto() {
        var apiaryDto1 = testData.getApiaryDto1();
        var apiaryEntity1 = testData.getApiaryEntity1();
        var convertedEntity = objectMapper.convertApiaryDto(apiaryDto1);
        assertEquals(apiaryEntity1.getId(), convertedEntity.getId());
        assertEquals(apiaryEntity1.getName(), convertedEntity.getName());
        assertEquals(apiaryEntity1.getOwner(), convertedEntity.getOwner());
        assertEquals(apiaryEntity1.getTerrain(), convertedEntity.getTerrain());
        assertEquals(apiaryEntity1.getEnvironment(), convertedEntity.getEnvironment());
        assertEquals(apiaryEntity1.getLatitude(), convertedEntity.getLatitude());
        assertEquals(apiaryEntity1.getLongitude(), convertedEntity.getLongitude());
        assertEquals(apiaryEntity1.getNotes(), convertedEntity.getNotes());
    }

    @Test
    void testConvertApiaryEntity() {
        var apiaryEntity1 = testData.getApiaryEntity1();
        var apiaryDto1 = testData.getApiaryDto1();
        var convertedDto = objectMapper.convertApiaryEntity(apiaryEntity1);
        assertEquals(convertedDto.getId(), apiaryDto1.getId());
        assertEquals(convertedDto.getName(), apiaryDto1.getName());
        assertEquals(convertedDto.getTerrain(), apiaryDto1.getTerrain());
        assertEquals(convertedDto.getEnvironment(), apiaryDto1.getEnvironment());
        assertEquals(convertedDto.getLatitude(), apiaryDto1.getLatitude());
        assertEquals(convertedDto.getLongitude(), apiaryDto1.getLongitude());
        assertEquals(convertedDto.getNotes(), apiaryDto1.getNotes());
    }

    @Test
    void testConvertApiaryEntityList() {
        var apiaryDto1 = testData.getApiaryDto1();
        var apiaryDto2 = testData.getApiaryDto2();
        var list = List.of(testData.getApiaryEntity1(), testData.getApiaryEntity2());
        var convertedList = objectMapper.convertApiaryEntityList(list);
        assertEquals(2, convertedList.size());
        assertEquals(apiaryDto1.getId(), convertedList.get(0).getId());
        assertEquals(apiaryDto1.getName(), convertedList.get(0).getName());
        assertEquals(apiaryDto1.getTerrain(), convertedList.get(0).getTerrain());
        assertEquals(apiaryDto1.getEnvironment(), convertedList.get(0).getEnvironment());
        assertEquals("64.456000", convertedList.get(0).getLatitude());
        assertEquals("75.524000", convertedList.get(0).getLongitude());
        assertEquals(apiaryDto1.getNotes(), convertedList.get(0).getNotes());

        assertEquals(apiaryDto2.getId(), convertedList.get(1).getId());
        assertEquals(apiaryDto2.getName(), convertedList.get(1).getName());
        assertEquals(apiaryDto2.getTerrain(), convertedList.get(1).getTerrain());
        assertEquals(apiaryDto2.getEnvironment(), convertedList.get(1).getEnvironment());
        assertEquals("20.156000", convertedList.get(1).getLatitude());
        assertEquals("48.152000", convertedList.get(1).getLongitude());
        assertEquals(apiaryDto2.getNotes(), convertedList.get(1).getNotes());
    }

    @Test
    void testConvertEventDto() {
        var event = testData.getEventDto();
        var entity = objectMapper.convertEventDto(event);
        assertEquals(1L, entity.getId());
        assertEquals("Add new supers", entity.getTitle());
        assertEquals(ApiaryEnums.EEventType.GENERAL, entity.getType());
        assertEquals(ApiaryEnums.EEventActivityType.FEED, entity.getActivity());
        assertEquals("Event note 1", entity.getNotes());
        assertEquals(DateTimeUtils.getDateFromString("2024-04-01"), entity.getDate());
        assertFalse(entity.getFinished());
    }

    @Test
    void testConvertEventList() {
        var entity = objectMapper.convertEventList(testData.getEventDtos());

        var dayEvents = entity.get("2024-04-05");
        assertEquals(2, dayEvents.size());
        assertNull(entity.get("2024-04-03"));

        assertEquals(1L, dayEvents.get(0).getId());
        assertEquals("Heal new hive", dayEvents.get(0).getTitle());
        assertEquals(ApiaryEnums.EEventType.HIVE, dayEvents.get(0).getType());
        assertEquals(ApiaryEnums.EEventActivityType.HEAL, dayEvents.get(0).getActivity());
        assertEquals("2024-04-05", dayEvents.get(0).getDate());
        assertFalse(dayEvents.get(0).isFinished());

        assertEquals(2L, dayEvents.get(1).getId());
        assertEquals("Inspect all hives in first apiary", dayEvents.get(1).getTitle());
        assertEquals(ApiaryEnums.EEventType.APIARY, dayEvents.get(1).getType());
        assertEquals(ApiaryEnums.EEventActivityType.INSPECT, dayEvents.get(1).getActivity());
        assertEquals("Event note 1", dayEvents.get(1).getNotes());
        assertEquals("2024-04-05", dayEvents.get(1).getDate());
        assertFalse(dayEvents.get(0).isFinished());
    }

    @Test
    void testConvertHiveEntity() {
        var hive = testData.getHives().get(0);
        var dto = objectMapper.convertHiveEntity(hive);
        assertEquals(4L, dto.getId());
        assertEquals("Hive near forest", dto.getName());
        assertEquals(HiveEnums.EColor.YELLOW, dto.getColor());
        assertEquals(HiveEnums.EBeeSource.SWARM, dto.getSource());
        assertEquals("2022-06-12", dto.getEstablishment());
        assertEquals("New stable hive", dto.getNotes());
        assertEquals("Anna", dto.getQueenName());
        assertEquals(QueenEnums.EColor.BLUE, dto.getQueenColor());
        assertEquals("", dto.getQueenNotes());
        assertEquals("Australian", dto.getBreed());
    }

    @Test
    void testConvertHiveEntityList() {
        var convertedList = objectMapper.convertHiveEntityList(testData.getHives());
        assertEquals(2, convertedList.size());
        var dto = convertedList.get(0);
        assertEquals(4L, dto.getId());
        assertEquals("Hive near forest", dto.getName());
        assertEquals(HiveEnums.EColor.YELLOW, dto.getColor());
        assertEquals(HiveEnums.EBeeSource.SWARM, dto.getSource());
        assertEquals("2022-06-12", dto.getEstablishment());
        assertEquals("New stable hive", dto.getNotes());
        dto = convertedList.get(1);
        assertEquals(6L, dto.getId());
        assertEquals("Old field hive", dto.getName());
        assertEquals(HiveEnums.EColor.NONE, dto.getColor());
        assertEquals(HiveEnums.EBeeSource.SPLIT, dto.getSource());
        assertEquals("2020-08-25", dto.getEstablishment());
        assertEquals("My first hive", dto.getNotes());
    }

    @Test
    void testConvertHiveDto() {
        var hive = objectMapper.convertHiveDto(testData.getHiveDto());
        assertEquals(9L, hive.getId());
        assertEquals("Hive 9", hive.getName());
        assertEquals(HiveEnums.EColor.YELLOW, hive.getColor());
        assertEquals(HiveEnums.EBeeSource.PACKAGE, hive.getSource());
        assertEquals(LocalDate.of(2022, 1, 27), hive.getEstablishment());
        assertEquals("", hive.getNotes());
        assertNotNull(hive.getQueen());
        assertEquals("Ola", hive.getQueen().getName());
        assertEquals(QueenEnums.EColor.BLUE, hive.getQueen().getColor());
        assertEquals("Australian", hive.getQueen().getBreed());
        assertEquals("Best queen", hive.getQueen().getNotes());
        assertEquals(LocalDate.of(2023, 4, 19), hive.getQueen().getQueenHatch());
    }

    @Test
    void testConvertInspectionEntityList() {
        var inspections = testData.getInspectionEntities();
        var converted = objectMapper.convertInspectionEntityList(inspections);

        assertEquals(3, converted.size());

        var i1 = converted.get(0);
        assertEquals(InspectionEnums.EType.INSPECTION, i1.getType());
        assertEquals(LocalDate.now().toString(), i1.getDate());
        assertEquals(InspectionEnums.EPopulation.NORMAL, i1.getPopulation());
        assertEquals(InspectionEnums.EFoodStorage.NORMAL, i1.getFood());
        assertEquals(InspectionEnums.ESourceNearby.LOW, i1.getSourceNearby());
        assertTrue(i1.isHasBrood());
        assertTrue(i1.isHasDisease());
        assertTrue(i1.isHasQueen());

        var i2 = converted.get(1);
        assertEquals(InspectionEnums.EType.HARVEST, i2.getType());
        assertEquals(LocalDate.now().toString(), i2.getDate());
        assertEquals(InspectionEnums.EPopulation.LOW, i2.getPopulation());
        assertEquals(InspectionEnums.EFoodStorage.VERY_GOOD, i2.getFood());
        assertEquals(InspectionEnums.ESourceNearby.HIGH, i2.getSourceNearby());
        assertFalse(i2.isHasBrood());
        assertTrue(i2.isHasDisease());
        assertFalse(i2.isHasQueen());

        var i3 = converted.get(2);
        assertEquals(InspectionEnums.EType.FEEDING, i3.getType());
        assertEquals(LocalDate.now().toString(), i3.getDate());
        assertEquals(InspectionEnums.EPopulation.VERY_LOW, i3.getPopulation());
        assertEquals(InspectionEnums.EFoodStorage.VERY_LOW, i3.getFood());
        assertEquals(InspectionEnums.ESourceNearby.NONE, i3.getSourceNearby());
        assertTrue(i3.isHasBrood());
        assertFalse(i3.isHasDisease());
        assertFalse(i3.isHasQueen());
    }

    @Test
    void testConvertInspectionDto() {
        var entity = objectMapper.convertInspectionDto(testData.getInspectionDetailDtoList().get(0));
        assertEquals(1L, entity.getId());
        assertEquals(InspectionEnums.EType.INSPECTION, entity.getType());
        assertEquals(InspectionEnums.EWeather.CLEAR, entity.getWeather());
        assertEquals(InspectionEnums.EPopulation.NORMAL, entity.getPopulation());
        assertEquals(InspectionEnums.EFoodStorage.NORMAL, entity.getFoodStorage());
        assertEquals(InspectionEnums.ESourceNearby.LOW, entity.getSourceNearby());
        assertEquals(InspectionEnums.EBroodPattern.SOLID, entity.getBroodPattern());
        assertTrue(entity.isQueen());
        assertFalse(entity.isEggs());
        assertTrue(entity.isUncappedBrood());
        assertFalse(entity.isCappedBrood());

        assertTrue(entity.getStressors().isVarroaMites());
        assertTrue(entity.getStressors().isChalkbrood());
        assertTrue(entity.getStressors().isSacbrood());
        assertTrue(entity.getStressors().isFoulbrood());
        assertTrue(entity.getStressors().isNosema());
        assertTrue(entity.getStressors().isBeetles());
        assertTrue(entity.getStressors().isMice());
        assertTrue(entity.getStressors().isAnts());
        assertTrue(entity.getStressors().isMoths());
        assertTrue(entity.getStressors().isWasps());
        assertTrue(entity.getStressors().isHornet());

        assertEquals(InspectionEnums.EDisease.NOSEMA, entity.getTreatment().getDisease());
        assertEquals("Treatment example", entity.getTreatment().getTreatment());
        assertEquals(5, entity.getTreatment().getQuantity());
        assertEquals(InspectionEnums.EUnitsAndDoses.DROP, entity.getTreatment().getDose());
        assertEquals(LocalDate.now().minusDays(1), entity.getTreatment().getStartDate());
        assertEquals(LocalDate.now().plusDays(1), entity.getTreatment().getEndDate());

        assertEquals(InspectionEnums.EFoodType.NECTAR, entity.getFeeding().getFood());
        assertEquals(InspectionEnums.EFoodRatio.ONE_ONE, entity.getFeeding().getRatio());
        assertEquals(1, entity.getFeeding().getFoodQuantity());
        assertEquals(InspectionEnums.EUnitsAndDoses.LITER, entity.getFeeding().getFoodUnit());

        assertEquals(InspectionEnums.EHarvestProduct.HONEY, entity.getHarvest().getProduct());
        assertEquals(3, entity.getHarvest().getProductQuantity());
        assertEquals(InspectionEnums.EUnitsAndDoses.KILOGRAM, entity.getHarvest().getProductUnit());
        assertEquals(2, entity.getHarvest().getSuperCount());
        assertEquals(12, entity.getHarvest().getFrameCount());
    }

    @Test
    void testConvertInspectionEntity() {
        var detailDto = objectMapper.convertInspectionEntity(testData.getInspectionEntities().get(1));

        assertEquals(InspectionEnums.EType.HARVEST, detailDto.getType());
        assertEquals(LocalDate.now().toString(), detailDto.getDate());
        assertEquals(InspectionEnums.EWeather.DRIZZLE, detailDto.getWeather());
        assertEquals(InspectionEnums.EPopulation.LOW, detailDto.getPopulation());
        assertEquals(InspectionEnums.EFoodStorage.VERY_GOOD, detailDto.getFoodStorage());
        assertEquals(InspectionEnums.ESourceNearby.HIGH, detailDto.getSourceNearby());
        assertEquals(InspectionEnums.EBroodPattern.NO_BROOD, detailDto.getBroodPattern());
        assertEquals(InspectionEnums.EColonyTemperament.CALM, detailDto.getColonyTemperament());
        assertFalse(detailDto.isHasQueen());
        assertTrue(detailDto.isHasEggs());
        assertFalse(detailDto.isHasUncappedBrood());
        assertTrue(detailDto.isHasCappedBrood());
    }

    @Test
    void testConvertStressorsEntity() {
        var stressorsDto1 = objectMapper.convertStressorsEntity(testData.getInspectionEntities().get(0).getStressors());
        assertTrue(stressorsDto1.isVarroaMites());
        assertTrue(stressorsDto1.isChalkbrood());
        assertTrue(stressorsDto1.isSacbrood());
        assertTrue(stressorsDto1.isFoulbrood());
        assertTrue(stressorsDto1.isNosema());
        assertTrue(stressorsDto1.isBeetles());
        assertTrue(stressorsDto1.isMice());
        assertTrue(stressorsDto1.isAnts());
        assertTrue(stressorsDto1.isMoths());
        assertTrue(stressorsDto1.isWasps());
        assertTrue(stressorsDto1.isHornet());

        var stressorsDto2 = objectMapper.convertStressorsEntity(testData.getInspectionEntities().get(1).getStressors());
        assertTrue(stressorsDto2.isVarroaMites());
        assertFalse(stressorsDto2.isChalkbrood());
        assertTrue(stressorsDto2.isSacbrood());
        assertFalse(stressorsDto2.isFoulbrood());
        assertTrue(stressorsDto2.isNosema());
        assertFalse(stressorsDto2.isBeetles());
        assertTrue(stressorsDto2.isMice());
        assertFalse(stressorsDto2.isAnts());
        assertTrue(stressorsDto2.isMoths());
        assertFalse(stressorsDto2.isWasps());
        assertTrue(stressorsDto2.isHornet());

        var stressorsDto3 = objectMapper.convertStressorsEntity(testData.getInspectionEntities().get(2).getStressors());
        assertFalse(stressorsDto3.isVarroaMites());
        assertFalse(stressorsDto3.isChalkbrood());
        assertFalse(stressorsDto3.isSacbrood());
        assertFalse(stressorsDto3.isFoulbrood());
        assertFalse(stressorsDto3.isNosema());
        assertFalse(stressorsDto3.isBeetles());
        assertFalse(stressorsDto3.isMice());
        assertFalse(stressorsDto3.isAnts());
        assertFalse(stressorsDto3.isMoths());
        assertFalse(stressorsDto3.isWasps());
        assertFalse(stressorsDto3.isHornet());
    }

    @Test
    void testConvertTreatmentEntity() {
        var detailDto = objectMapper.convertInspectionEntity(testData.getInspectionEntities().get(1));
        assertEquals(InspectionEnums.EDisease.FOULBROOD, detailDto.getTreatment().getDisease());
        assertEquals("Treatment foulbrood", detailDto.getTreatment().getTreatment());
        assertEquals(20, detailDto.getTreatment().getQuantity());
        assertEquals(InspectionEnums.EUnitsAndDoses.MILLILITER, detailDto.getTreatment().getDose());
        assertEquals(LocalDate.now().minusDays(2).toString(), detailDto.getTreatment().getStartDate());
        assertEquals(LocalDate.now().plusDays(1).toString(), detailDto.getTreatment().getEndDate());
    }

    @Test
    void testConvertFeedingEntity() {
        var detailDto = objectMapper.convertInspectionEntity(testData.getInspectionEntities().get(1));
        assertEquals(InspectionEnums.EFoodType.SUGAR, detailDto.getFeeding().getFood());
        assertEquals(InspectionEnums.EFoodRatio.ONE_ONE, detailDto.getFeeding().getRatio());
        assertEquals(1000, detailDto.getFeeding().getFoodQuantity());
        assertEquals(InspectionEnums.EUnitsAndDoses.GRAM, detailDto.getFeeding().getFoodUnit());
    }

    @Test
    void testConvertHarvestEntity() {
        var detailDto = objectMapper.convertInspectionEntity(testData.getInspectionEntities().get(1));
        assertEquals(InspectionEnums.EHarvestProduct.WAX, detailDto.getHarvest().getProduct());
        assertEquals(0.5, detailDto.getHarvest().getProductQuantity());
        assertEquals(InspectionEnums.EUnitsAndDoses.KILOGRAM, detailDto.getHarvest().getProductUnit());
        assertEquals(1, detailDto.getHarvest().getSuperCount());
        assertEquals(5, detailDto.getHarvest().getFrameCount());
    }

    @Test
    void testConvertObjectTimelineCounts() {
        var graphPoints = objectMapper.convertObjectTimelineCounts(testData.getUsersTimeline());
        assertEquals(3, graphPoints.size());
        assertEquals(10, graphPoints.get(0).getCount());
        assertEquals(15, graphPoints.get(1).getCount());
        assertEquals(20, graphPoints.get(2).getCount());
    }
}

package cz.zcu.kiv.server.beecommunity.testData;

import cz.zcu.kiv.server.beecommunity.enums.*;
import cz.zcu.kiv.server.beecommunity.jpa.dto.apiary.ApiaryDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.event.EventDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.*;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.AddressDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.NewUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.NewUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.*;
import cz.zcu.kiv.server.beecommunity.utils.DateTimeUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class ObjectMapperTestData {

    NewUserDto newUserDto = new NewUserDto("test@example.com", "password");
    UserEntity expectedUserEntity = UserEntity.builder().email("test@example.com").password("password").build();

    NewUserInfoDto newUserInfoDto1 = new NewUserInfoDto(
            "John",
            "Doe",
            "1990-01-01",
            UserEnums.EExperience.BEGINNER,
            "NY",
            "US",
            "New York",
            "Main St",
            456,
            new AddressDto()
    );

    UserInfoEntity expectedUserInfoEntity = UserInfoEntity
            .builder()
            .name("John")
            .surname("Doe")
            .experience(UserEnums.EExperience.BEGINNER)
            .address(new AddressEntity(1L, "NY", "US", "New York", "Main St", 456))
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .build();

    UserEntity user1 = UserEntity
            .builder()
            .id(1L)
            .email("john@example.com")
            .userInfo(UserInfoEntity
                    .builder()
                    .name("John")
                    .surname("Doe")
                    .address(AddressEntity
                            .builder()
                            .country("US")
                            .state("NY")
                            .town("New York")
                            .build())
                    .build())
            .build();
    UserEntity user2 = UserEntity
            .builder()
            .id(2L)
            .email("jane@example.com")
            .userInfo(UserInfoEntity
                    .builder()
                    .name("Jane")
                    .surname("Doe")
                    .address(AddressEntity
                            .builder()
                            .country("US")
                            .state("CA")
                            .town("Los Angeles")
                            .build())
                    .build())
            .build();

    UserEntity user3 = UserEntity
            .builder()
            .id(3L)
            .email("martin@example.com")
            .userInfo(UserInfoEntity
                    .builder()
                    .name("Martin")
                    .surname("Lacha")
                    .address(AddressEntity
                            .builder()
                            .country("CZ")
                            .state("CZ")
                            .town("Chlumany")
                            .build())
                    .build())
            .build();

    // Community posts
    private final LocalDate date = LocalDate.now();
    private final LocalDateTime dateTime = date.atStartOfDay();
    CommunityPostEntity post1 = CommunityPostEntity
            .builder()
            .id(1L)
            .title("Title 1")
            .post("Post 1")
            .created(date)
            .author(user1)
            .access(CommunityEnums.EAccess.PUBLIC)
            .type(CommunityEnums.EType.INFO)
            .comments(new ArrayList<>())
            .build();

    CommunityPostEntity post2 = CommunityPostEntity
            .builder()
            .id(2L)
            .title("Title 2")
            .post("Post 2")
            .created(date)
            .author(user2)
            .access(CommunityEnums.EAccess.PRIVATE)
            .type(CommunityEnums.EType.WARN)
            .comments(new ArrayList<>())
            .build();

    CommunityPostEntity post3 = CommunityPostEntity
            .builder()
            .id(3L)
            .title("Title 3")
            .post("Post 3")
            .created(dateTime.toLocalDate())
            .author(user3)
            .access(CommunityEnums.EAccess.PUBLIC)
            .type(CommunityEnums.EType.NONE)
            .comments(List.of(PostCommentEntity
                            .builder()
                            .id(1L)
                            .date(LocalDate.now())
                            .author(user1)
                            .comment("First comment")
                            .build(),
                    PostCommentEntity
                            .builder()
                            .id(2L)
                            .date(LocalDate.now())
                            .author(user2)
                            .comment("Second comment")
                            .build()))
            .build();

    CommunityPostEntity post4 = CommunityPostEntity
            .builder()
            .id(4L)
            .title("Title 4")
            .post("Post 4")
            .created(dateTime.toLocalDate())
            .author(user1)
            .access(CommunityEnums.EAccess.PRIVATE)
            .type(CommunityEnums.EType.EMERGENCY)
            .comments(new ArrayList<>())
            .build();

    CommunityPostDto postDto = new CommunityPostDto(
                1L,
                "John Doe",
                "Title 1",
                "Post 1",
                null,
                CommunityEnums.EAccess.PUBLIC,
                CommunityEnums.EType.INFO,
                LocalDate.now().toString(),
                null);
    // News
    NewsEntity news1 = NewsEntity
            .builder()
            .id(1L)
            .title("News title 1")
            .article("Article 1")
            .date(date)
            .author(user2)
            .build();
    NewsEntity news2 = NewsEntity
            .builder()
            .id(2L)
            .title("News title 2")
            .article("Article 2")
            .date(date)
            .author(user3)
            .titleImage(new byte[0])
            .build();
    NewsDetailDto newsDetailDto1 = NewsDetailDto
            .builder()
            .id(1L)
            .title("News title 1")
            .article("Article 1")
            .author(user2.getFullName())
            .date(date.toString())
            .build();
    NewsDetailDto newsDetailDto2 = NewsDetailDto
            .builder()
            .id(2L)
            .title("News title 2")
            .article("Article 2")
            .author(user3.getFullName())
            .date(date.toString())
            .build();
    // Event
    EventDto eventDto = EventDto.builder()
            .id(1L)
                .title("Add new supers")
                .type(ApiaryEnums.EEventType.GENERAL)
                .activity(ApiaryEnums.EEventActivityType.FEED)
                .notes("Event note 1")
                .date("2024-04-01")
                .isFinished(false)
                .build();

    List<EventEntity> eventDtos = List.of(
        EventEntity.builder()
                .id(1L)
                .title("Heal new hive")
                .type(ApiaryEnums.EEventType.HIVE)
                .activity(ApiaryEnums.EEventActivityType.HEAL)
                .date(DateTimeUtils.getDateFromString("2024-04-05"))
                .finished(false)
                .build(),
        EventEntity.builder()
                .id(2L)
                .title("Inspect all hives in first apiary")
                .type(ApiaryEnums.EEventType.APIARY)
                .activity(ApiaryEnums.EEventActivityType.INSPECT)
                .notes("Event note 1")
                .date(DateTimeUtils.getDateFromString("2024-04-05"))
                .finished(true)
                .build()
    );

    // Apiary
    ApiaryEntity apiaryEntity1 = ApiaryEntity
            .builder()
            .id(1L)
            .name("Apiary 1")
            .environment(ApiaryEnums.EEnvironment.RURAL)
            .terrain(ApiaryEnums.ETerrain.DESERT)
            .latitude(64.456)
            .longitude(75.524)
            .notes("Notes apiary 1")
            .build();
    ApiaryEntity apiaryEntity2 = ApiaryEntity
            .builder()
            .id(2L)
            .name("Apiary 2")
            .environment(ApiaryEnums.EEnvironment.SUBURBAN)
            .terrain(ApiaryEnums.ETerrain.ROOFTOP)
            .latitude(20.156)
            .longitude(48.152)
            .notes("Notes apiary 2")
            .build();

    ApiaryDto apiaryDto1 = ApiaryDto
            .builder()
            .id(1L)
            .name("Apiary 1")
            .environment(ApiaryEnums.EEnvironment.RURAL)
            .terrain(ApiaryEnums.ETerrain.DESERT)
            .latitude("64.456")
            .longitude("75.524")
            .notes("Notes apiary 1")
            .build();
    ApiaryDto apiaryDto2 = ApiaryDto
            .builder()
            .id(2L)
            .name("Apiary 2")
            .environment(ApiaryEnums.EEnvironment.SUBURBAN)
            .terrain(ApiaryEnums.ETerrain.ROOFTOP)
            .latitude("20.156")
            .longitude("48.152")
            .notes("Notes apiary 2")
            .build();

    // Hives
    List<HiveEntity> hives = List.of(
            HiveEntity
                    .builder()
                    .id(4L)
                    .name("Hive near forest")
                    .color(HiveEnums.EColor.YELLOW)
                    .source(HiveEnums.EBeeSource.SWARM)
                    .establishment(LocalDate.of(2022, 6, 12))
                    .notes("New stable hive")
                    .apiary(ApiaryEntity.builder().id(1L).build())
                    .queen(QueenEntity
                            .builder()
                            .name("Anna")
                            .color(QueenEnums.EColor.BLUE)
                            .notes("")
                            .breed("Australian")
                            .build())
                    .build(),
            HiveEntity
                    .builder()
                    .id(6L)
                    .name("Old field hive")
                    .color(HiveEnums.EColor.NONE)
                    .source(HiveEnums.EBeeSource.SPLIT)
                    .establishment(LocalDate.of(2020, 8, 25))
                    .notes("My first hive")
                    .apiary(ApiaryEntity.builder().id(1L).build())
                    .queen(QueenEntity
                            .builder()
                            .name("Lady")
                            .color(QueenEnums.EColor.WHITE)
                            .notes("")
                            .breed("")
                            .build())
                    .build()
    );

    HiveDto hiveDto = HiveDto
            .builder()
            .id(9L)
            .name("Hive 9")
            .color(HiveEnums.EColor.YELLOW)
            .source(HiveEnums.EBeeSource.PACKAGE)
            .establishment("2022-01-27")
            .notes("")
            .apiaryId(4L)
            .queenName("Ola")
            .queenColor(QueenEnums.EColor.BLUE)
            .queenNotes("Best queen")
            .breed("Australian")
            .hatch("2023-04-19")
            .build();

    // Inspection

    List<InspectionEntity> inspectionEntities = List.of(
            InspectionEntity
                    .builder()
                    .hive(hives.get(0))
                    .owner(user1)
                    .type(InspectionEnums.EType.INSPECTION)
                    .inspectionDate(date)
                    .weather(InspectionEnums.EWeather.CLEAR)
                    .population(InspectionEnums.EPopulation.NORMAL)
                    .foodStorage(InspectionEnums.EFoodStorage.NORMAL)
                    .sourceNearby(InspectionEnums.ESourceNearby.LOW)
                    .broodPattern(InspectionEnums.EBroodPattern.SOLID)
                    .queen(true)
                    .eggs(false)
                    .uncappedBrood(true)
                    .cappedBrood(false)
                    .stressors(StressorsEntity
                            .builder()
                            .id(1L)
                            .varroaMites(true)
                            .chalkbrood(true)
                            .sacbrood(true)
                            .foulbrood(true)
                            .nosema(true)
                            .beetles(true)
                            .mice(true)
                            .ants(true)
                            .moths(true)
                            .wasps(true)
                            .hornet(true)
                            .build())
                    .treatment(HiveTreatmentEntity
                            .builder()
                            .id(1L)
                            .disease(InspectionEnums.EDisease.NOSEMA)
                            .treatment("Treatment example")
                            .quantity(5)
                            .dose(InspectionEnums.EUnitsAndDoses.DROP)
                            .startDate(LocalDate.now().minusDays(1))
                            .endDate(LocalDate.now().plusDays(1))
                            .build())
                    .feeding(HiveFeedingEntity
                            .builder()
                            .id(1L)
                            .food(InspectionEnums.EFoodType.NECTAR)
                            .ratio(InspectionEnums.EFoodRatio.ONE_ONE)
                            .foodQuantity(1)
                            .foodUnit(InspectionEnums.EUnitsAndDoses.LITER)
                            .build())
                    .harvest(HiveHarvestEntity
                            .builder()
                            .id(1L)
                            .product(InspectionEnums.EHarvestProduct.HONEY)
                            .productQuantity(3)
                            .productUnit(InspectionEnums.EUnitsAndDoses.KILOGRAM)
                            .superCount(2)
                            .frameCount(12)
                            .build())
                    .build(),
            InspectionEntity
                    .builder()
                    .hive(hives.get(0))
                    .owner(user1)
                    .type(InspectionEnums.EType.HARVEST)
                    .inspectionDate(date)
                    .temperament(InspectionEnums.EColonyTemperament.CALM)
                    .weather(InspectionEnums.EWeather.DRIZZLE)
                    .population(InspectionEnums.EPopulation.LOW)
                    .foodStorage(InspectionEnums.EFoodStorage.VERY_GOOD)
                    .sourceNearby(InspectionEnums.ESourceNearby.HIGH)
                    .broodPattern(InspectionEnums.EBroodPattern.NO_BROOD)
                    .queen(false)
                    .eggs(true)
                    .uncappedBrood(false)
                    .cappedBrood(true)
                    .stressors(StressorsEntity
                            .builder()
                            .id(2L)
                            .varroaMites(true)
                            .chalkbrood(false)
                            .sacbrood(true)
                            .foulbrood(false)
                            .nosema(true)
                            .beetles(false)
                            .mice(true)
                            .ants(false)
                            .moths(true)
                            .wasps(false)
                            .hornet(true)
                            .build())
                    .treatment(HiveTreatmentEntity
                            .builder()
                            .id(2L)
                            .disease(InspectionEnums.EDisease.FOULBROOD)
                            .treatment("Treatment foulbrood")
                            .quantity(20)
                            .dose(InspectionEnums.EUnitsAndDoses.MILLILITER)
                            .startDate(LocalDate.now().minusDays(2))
                            .endDate(LocalDate.now().plusDays(1))
                            .build())
                    .feeding(HiveFeedingEntity
                            .builder()
                            .id(2L)
                            .food(InspectionEnums.EFoodType.SUGAR)
                            .ratio(InspectionEnums.EFoodRatio.ONE_ONE)
                            .foodQuantity(1000)
                            .foodUnit(InspectionEnums.EUnitsAndDoses.GRAM)
                            .build())
                    .harvest(HiveHarvestEntity
                            .builder()
                            .id(2L)
                            .product(InspectionEnums.EHarvestProduct.WAX)
                            .productQuantity(0.5)
                            .productUnit(InspectionEnums.EUnitsAndDoses.KILOGRAM)
                            .superCount(1)
                            .frameCount(5)
                            .build())
                    .build(),
            InspectionEntity
                    .builder()
                    .hive(hives.get(0))
                    .owner(user1)
                    .type(InspectionEnums.EType.FEEDING)
                    .inspectionDate(date)
                    .weather(InspectionEnums.EWeather.CLEAR)
                    .population(InspectionEnums.EPopulation.VERY_LOW)
                    .foodStorage(InspectionEnums.EFoodStorage.VERY_LOW)
                    .sourceNearby(InspectionEnums.ESourceNearby.NONE)
                    .broodPattern(InspectionEnums.EBroodPattern.OTHER)
                    .queen(false)
                    .eggs(false)
                    .uncappedBrood(false)
                    .cappedBrood(false)
                    .stressors(StressorsEntity
                            .builder()
                            .id(3L)
                            .varroaMites(false)
                            .chalkbrood(false)
                            .sacbrood(false)
                            .foulbrood(false)
                            .nosema(false)
                            .beetles(false)
                            .mice(false)
                            .ants(false)
                            .moths(false)
                            .wasps(false)
                            .hornet(false)
                            .build())
                    .treatment(HiveTreatmentEntity
                            .builder()
                            .id(3L)
                            .disease(InspectionEnums.EDisease.VARROASIS)
                            .treatment("Treatment varroa")
                            .quantity(2)
                            .dose(InspectionEnums.EUnitsAndDoses.STRIP)
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now())
                            .build())
                    .feeding(HiveFeedingEntity
                            .builder()
                            .id(3L)
                            .food(InspectionEnums.EFoodType.HONEY)
                            .ratio(InspectionEnums.EFoodRatio.NONE)
                            .foodQuantity(500)
                            .foodUnit(InspectionEnums.EUnitsAndDoses.MILLILITER)
                            .build())
                    .harvest(HiveHarvestEntity
                            .builder()
                            .id(3L)
                            .product(InspectionEnums.EHarvestProduct.ROYAL_JELLY)
                            .productQuantity(150)
                            .productUnit(InspectionEnums.EUnitsAndDoses.GRAM)
                            .build())
                    .build()
    );

    List<InspectionDetailDto> inspectionDetailDtoList = List.of(
            InspectionDetailDto
                    .builder()
                    .id(1L)
                    .hiveId(4L)
                    .type(InspectionEnums.EType.INSPECTION)
                    .date(date.toString())
                    .weather(InspectionEnums.EWeather.CLEAR)
                    .population(InspectionEnums.EPopulation.NORMAL)
                    .foodStorage(InspectionEnums.EFoodStorage.NORMAL)
                    .sourceNearby(InspectionEnums.ESourceNearby.LOW)
                    .broodPattern(InspectionEnums.EBroodPattern.SOLID)
                    .hasQueen(true)
                    .hasEggs(false)
                    .hasUncappedBrood(true)
                    .hasCappedBrood(false)
                    .stressors(StressorsDto
                            .builder()
                            .varroaMites(true)
                            .chalkbrood(true)
                            .sacbrood(true)
                            .foulbrood(true)
                            .nosema(true)
                            .beetles(true)
                            .mice(true)
                            .ants(true)
                            .moths(true)
                            .wasps(true)
                            .hornet(true)
                            .build())
                    .treatment(TreatmentDto.builder()
                            .disease(InspectionEnums.EDisease.NOSEMA)
                            .treatment("Treatment example")
                            .quantity(5)
                            .dose(InspectionEnums.EUnitsAndDoses.DROP)
                            .startDate(LocalDate.now().minusDays(1).toString())
                            .endDate(LocalDate.now().plusDays(1).toString())
                            .build())
                    .feeding(FeedingDto
                            .builder()
                            .food(InspectionEnums.EFoodType.NECTAR)
                            .ratio(InspectionEnums.EFoodRatio.ONE_ONE)
                            .foodQuantity(1)
                            .foodUnit(InspectionEnums.EUnitsAndDoses.LITER)
                            .build())
                    .harvest(HarvestDto
                            .builder()
                            .product(InspectionEnums.EHarvestProduct.HONEY)
                            .productQuantity(3)
                            .productUnit(InspectionEnums.EUnitsAndDoses.KILOGRAM)
                            .superCount(2)
                            .frameCount(12)
                            .build())
                    .build(),
            InspectionDetailDto
                    .builder()
                    .id(2L)
                    .hiveId(4L)
                    .type(InspectionEnums.EType.FEEDING)
                    .date(date.toString())
                    .population(InspectionEnums.EPopulation.VERY_LOW)
                    .foodStorage(InspectionEnums.EFoodStorage.VERY_LOW)
                    .sourceNearby(InspectionEnums.ESourceNearby.NONE)
                    .broodPattern(InspectionEnums.EBroodPattern.OTHER)
                    .hasQueen(false)
                    .hasEggs(false)
                    .hasUncappedBrood(false)
                    .hasCappedBrood(false)
                    .stressors(StressorsDto
                            .builder()
                            .varroaMites(false)
                            .chalkbrood(false)
                            .sacbrood(false)
                            .foulbrood(false)
                            .nosema(false)
                            .beetles(false)
                            .mice(false)
                            .ants(false)
                            .moths(false)
                            .wasps(false)
                            .hornet(false)
                            .build())
                    .treatment(TreatmentDto
                            .builder()
                            .disease(InspectionEnums.EDisease.VARROASIS)
                            .treatment("Treatment varroa")
                            .quantity(2)
                            .dose(InspectionEnums.EUnitsAndDoses.STRIP)
                            .startDate(LocalDate.now().toString())
                            .endDate(LocalDate.now().toString())
                            .build())
                    .feeding(FeedingDto
                            .builder()
                            .food(InspectionEnums.EFoodType.HONEY)
                            .ratio(InspectionEnums.EFoodRatio.NONE)
                            .foodQuantity(500)
                            .foodUnit(InspectionEnums.EUnitsAndDoses.MILLILITER)
                            .build())
                    .harvest(HarvestDto
                            .builder()
                            .product(InspectionEnums.EHarvestProduct.ROYAL_JELLY)
                            .productQuantity(150)
                            .productUnit(InspectionEnums.EUnitsAndDoses.GRAM)
                            .build())
                    .build()
    );

    List<Object[]> usersTimeline = Arrays.asList(
        new Object[]{LocalDate.of(2023, 3, 15), 10L},
        new Object[]{LocalDate.of(2023, 3, 16), 15L},
        new Object[]{LocalDate.of(2023, 3, 17), 20L});

}

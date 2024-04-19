package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.enums.InspectionEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.apiary.ApiaryDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.event.EventDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.SensorDataDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.inspection.*;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.statistics.GraphOverviewItem;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.GetUpdateUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.NewUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.NewUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ObjectMapper {

    private final ModelMapper modelMapper;

    /**
     * Convert user dto into entity
     * @param userDto dto
     * @return user entity
     */
    public UserEntity convertToNewUserEntity(NewUserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }

    /**
     * Convert user information dto into entity
     * @param infoDto dto with user information
     * @return user info entity with address
     */
    public UserInfoEntity convertToUserInfoEntity(NewUserInfoDto infoDto) {
        infoDto.fillAddress();
        UserInfoEntity info =  modelMapper.map(infoDto, UserInfoEntity.class);
        LocalDate dob = DateTimeUtils.getDateFromString(infoDto.getDateOfBirth());
        info.setDateOfBirth(dob);
        return info;
    }

    /**
     * Convert entity into dto
     * @param userInfo entity of user information from database
     * @return dto of user information
     */
    public GetUpdateUserInfoDto convertUserInfoDto(UserInfoEntity userInfo) {
        GetUpdateUserInfoDto infoDto = modelMapper.map(userInfo, GetUpdateUserInfoDto.class);
        AddressEntity address = userInfo.getAddress();
        infoDto.setCountry(address.getCountry());
        infoDto.setState(address.getState());
        infoDto.setTown(address.getTown());
        infoDto.setStreet(address.getStreet());
        infoDto.setNumber(address.getNumber());
        infoDto.setExperience(userInfo.getExperience());
        return infoDto;
    }

    /**
     * Convert list of user entity into dto with found users
     * @param list of entities
     * @return list of dtos
     */
    public List<FoundUserDto> convertListUserEntity(List<UserEntity> list) {
        List<FoundUserDto> output = new ArrayList<>();
        list.forEach(userEntity -> output.add(getFriendFromFriendship(userEntity)));
        return output;
    }

    /**
     * Convert friendship into list of dtos of friend and it takes from sender and receiver
     * @param list of friendship of user
     * @param myId of user that want return all friends
     * @return list of found friends
     */
    public List<FoundUserDto> convertListFriendship(List<FriendshipEntity> list, Long myId) {
        List<FoundUserDto> output = new ArrayList<>();
        list.forEach(friendship -> output.add(getFriendFromFriendship(
                friendship.getReceiver().getId().equals(myId) ? friendship.getSender() : friendship.getReceiver()
        )));
        return output;
    }

    /**
     * Convert UserEntity into FoundFriendDto
     * @param user entity to convert into dto
     * @return dto
     */
    private FoundUserDto getFriendFromFriendship(UserEntity user) {
        return FoundUserDto
                .builder()
                .email(user.getEmail())
                .name(user.getUserInfo().getName())
                .surname(user.getUserInfo().getSurname())
                .country(user.getUserInfo().getAddress().getCountry())
                .state(user.getUserInfo().getAddress().getState())
                .town(user.getUserInfo().getAddress().getTown())
                .experience(user.getUserInfo().getExperience())
                .build();
    }

    /**
     * Convert list of post entities into list of dtos
     * @param posts to convert
     * @return list of dto
     */
    public List<CommunityPostDto> convertPostListToDtoList(List<CommunityPostEntity> posts) {
        List<CommunityPostDto> output = new ArrayList<>();
        posts.forEach(entity -> output.add(
                CommunityPostDto
                        .builder()
                        .id(entity.getId())
                        .title(entity.getTitle())
                        .author(entity.getAuthor().getFullName())
                        .date(entity.getCreated().toString())
                        .type(entity.getType())
                        .build()));
        return output;
    }

    /**
     * Return converted post dto with details
     * @param post entity of post
     * @return dto with detail information
     */
    public CommunityPostDto convertPostEntityToDto(CommunityPostEntity post) {
        return CommunityPostDto
                .builder()
                .id(post.getId())
                .author(post.getAuthor().getFullName())
                .title(post.getTitle())
                .post(post.getPost())
                .access(post.getAccess())
                .date(post.getCreated().toString())
                .type(post.getType())
                .comments(convertCommentsEntityToDtoList(post.getComments()))
                .build();
    }

    /**
     * Convert list of comment entities to list of dto objects
     * @param comments list of entities
     * @return list of dto
     */
    private List<PostCommentDto> convertCommentsEntityToDtoList(List<PostCommentEntity> comments) {
        List<PostCommentDto> output = new ArrayList<>();
        comments.forEach(comment -> output.add(convertCommentToDto(comment)));
        return output;
    }

    /**
     * Convert single comment entity to dto
     * @param comment entity to convert
     * @return converted dto from entity
     */
    public PostCommentDto convertCommentToDto(PostCommentEntity comment) {
        return PostCommentDto
                .builder()
                .id(comment.getId())
                .author(comment.getAuthor().getFullName())
                .comment(comment.getComment())
                .postId(comment.getPost().getId())
                .date(comment.getDate().toString())
                .build();
    }

    /**
     * Convert dto into entity
     * @param dto to convert
     * @return entity of post
     */
    public CommunityPostEntity convertPostDtoToEntity(CommunityPostDto dto) {
        var entity = modelMapper.map(dto, CommunityPostEntity.class);
        try {
            if (dto.getImage() != null) {
                entity.setImage(ImageUtil.compressImage(dto.getImage().getBytes()));
            }
        } catch (IOException e) {
            log.warn("Error while get image from post: {}", e.getMessage());
        }

        return entity;
    }

    /**
     * Convert NewsEntity into dto object
     * @param newsEntity entity to convert
     * @return dto of news article
     */
    public NewsDetailDto convertNewsEntityToDto(NewsEntity newsEntity) {
        return NewsDetailDto
                .builder()
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .article(newsEntity.getArticle())
                .author(newsEntity.getAuthor().getFullName())
                .date(newsEntity.getDate().toString())
                .build();
    }

    /**
     * Convert News dto into entity
     * @param newsDetailDto dto to convert
     * @return newsEntity
     */
    public NewsEntity convertNewsDtoToEntity(NewsDetailDto newsDetailDto) {
        var news = NewsEntity
                .builder()
                .title(newsDetailDto.getTitle())
                .article(newsDetailDto.getArticle());
        try {
            if (newsDetailDto.getTitleImage() != null)
                news.titleImage(ImageUtil.compressImage(newsDetailDto.getTitleImage().getBytes()));
            if (newsDetailDto.getFirstImage() != null)
                    news.firstImage(ImageUtil.compressImage(newsDetailDto.getFirstImage().getBytes()));
            if (newsDetailDto.getSecondImage() != null)
                    news.secondImage(ImageUtil.compressImage(newsDetailDto.getSecondImage().getBytes()));
        } catch (IOException e) {
            log.warn("Error while get image from news: {}", e.getMessage());
        }
        return news.build();
    }

    /**
     * Convert list of entities into list of dto for news overview
     * @param newsList list of entities to convert
     * @return list of news dto
     */
    public List<NewsDto> convertNewsList(List<NewsEntity> newsList) {
        var list = new ArrayList<NewsDto>();
        newsList.forEach(news -> list.add(
                NewsDto
                        .builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .article(news.getArticle())
                        .author(news.getAuthor().getFullName())
                        .date(news.getDate().toString())
                        .build()
        ));
        list.sort(Comparator.comparing(NewsDto::getId));
        return list;
    }

    /**
     * Convert apiary dto to entity
     * Compress image if it was uploaded
     * @param dto object to convert
     * @return converted apiary entity
     */
    public ApiaryEntity convertApiaryDto(ApiaryDto dto) {
        var entity = modelMapper.map(dto, ApiaryEntity.class);
        try {
            if (dto.getImage() != null) {
                entity.setImage(ImageUtil.compressImage(dto.getImage().getBytes()));
            }
        } catch (IOException e) {
            log.warn("Error while get image from apiary: {}", e.getMessage());
        }
        return entity;
    }

    /**
     * Convert apiary entity to dto
     * @param entity to convert
     * @return converted apiary dto
     */
    public ApiaryDto convertApiaryEntity(ApiaryEntity entity) {
        return modelMapper.map(entity, ApiaryDto.class);
    }

    /**
     * Convert list of apiary entities to list of dto
     * @param entitiesList list of entities
     * @return list of apiary dto
     */
    public List<ApiaryDto> convertApiaryEntityList(List<ApiaryEntity> entitiesList) {
        List<ApiaryDto> apiaries = new ArrayList<>();
        entitiesList.forEach(entity ->
                apiaries.add(ApiaryDto
                    .builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .environment(entity.getEnvironment())
                    .terrain(entity.getTerrain())
                    .latitude(String.format("%f", entity.getLatitude()))
                    .longitude(String.format("%f", entity.getLongitude()))
                    .notes(entity.getNotes())
                    .build()));
        return apiaries;
    }

    /**
     * Convert event dto into entity
     * @param event dto to convert
     * @return event entity
     */
    public EventEntity convertEventDto(EventDto event) {
        var entity = modelMapper.map(event, EventEntity.class);
        entity.setDate(DateTimeUtils.getDateFromString(event.getDate()));
        return entity;
    }

    /**
     * Convert list of event entities into list of dto
     * @param eventList list of entities
     * @return map of converted events
     */
    public LinkedHashMap<String, List<EventDto>> convertEventList(List<EventEntity> eventList) {
        List<EventDto> events = new ArrayList<>();
        eventList.forEach(entity -> events.add(EventDto
                .builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .activity(entity.getActivity())
                .type(entity.getType())
                .notes(entity.getNotes())
                .date(entity.getDate().toString())
                .isFinished(entity.getFinished())
                .build())
        );
        return events
                .stream()
                .collect(Collectors.groupingBy(EventDto::getDate, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * Convert hive entity and queen to dto
     * @param hive entity to convert
     * @return hive dto
     */
    public HiveDto convertHiveEntity(HiveEntity hive) {
        var detail = modelMapper.map(hive, HiveDto.class);
        detail.setQueenName(hive.getQueen().getName());
        detail.setQueenColor(hive.getQueen().getColor());
        detail.setQueenNotes(hive.getQueen().getNotes());
        if (hive.getQueen().getQueenHatch() != null) {
            detail.setHatch(hive.getQueen().getQueenHatch().toString());
        }
        detail.setBreed(hive.getQueen().getBreed());
        return detail;
    }

    /**
     * Convert list of hive entities to list of dto
     * @param hiveList list of entities
     * @return list of hive dto
     */
    public List<HiveDto> convertHiveEntityList(List<HiveEntity> hiveList) {
        List<HiveDto> hives = new ArrayList<>();
        hiveList.forEach(hive ->
                hives.add(HiveDto
                        .builder()
                        .id(hive.getId())
                        .apiaryId(hive.getApiary().getId())
                        .name(hive.getName())
                        .color(hive.getColor())
                        .source(hive.getSource())
                        .establishment(hive.getEstablishment().toString())
                        .notes(hive.getNotes())
                        .build()));
        return hives;
    }


    /**
     * Convert hive dto into entity and compress image if it was uploaded
     * @param hiveDto dto to convert
     * @return hive entity
     */
    public HiveEntity convertHiveDto(HiveDto hiveDto) {
        var hive = modelMapper.map(hiveDto, HiveEntity.class);
        var queen = modelMapper.map(hiveDto, QueenEntity.class);
        queen.setName(hiveDto.getQueenName());
        queen.setNotes(hiveDto.getQueenNotes());
        queen.setQueenHatch(DateTimeUtils.getDateFromString(hiveDto.getHatch()));
        queen.setColor(hiveDto.getQueenColor());
        hive.setEstablishment(DateTimeUtils.getDateFromString(hiveDto.getEstablishment()));
        try {
            if (hiveDto.getImage() != null) {
                hive.setImage(ImageUtil.compressImage(hiveDto.getImage().getBytes()));
            }
            if (hiveDto.getQueenImage() != null) {
                queen.setImage(ImageUtil.compressImage(hiveDto.getQueenImage().getBytes()));
            }
        } catch (IOException e) {
            log.warn("Error while get image from hive: {}", e.getMessage());
        }
        hive.setQueen(queen);
        return hive;
    }

    /**
     * Convert list of inspection entities to list of dto
     * @param list list of inspection entities
     * @return list of inspections list dto
     */
    public List<InspectionDto> convertInspectionEntityList(List<InspectionEntity> list) {
        List<InspectionDto> inspections = new ArrayList<>();
        list.forEach(inspection ->
                inspections.add(InspectionDto
                        .builder()
                        .id(inspection.getId())
                        .type(inspection.getType())
                        .date(inspection.getInspectionDate().toString())
                        .population(inspection.getPopulation())
                        .food(inspection.getFoodStorage())
                        .sourceNearby(inspection.getSourceNearby())
                        .hasQueen(inspection.isQueen())
                        .hasBrood(!inspection.getBroodPattern().equals(InspectionEnums.EBroodPattern.NO_BROOD))
                        .hasDisease(inspection.getStressors().hasDisease())
                        .build()));
        return inspections;
    }

    /**
     * Convert inspection from hive into entities
     * @param inspectionDto dto with inspection, feeding, treatment, disease information
     * @return converted entity
     */
    public InspectionEntity convertInspectionDto(InspectionDetailDto inspectionDto) {
        var inspection = modelMapper.map(inspectionDto, InspectionEntity.class);
        inspection.setInspectionDate(DateTimeUtils.getDateFromString(inspectionDto.getDate()));
        inspection.getTreatment().setStartDate(DateTimeUtils.getDateFromString(inspectionDto.getTreatment().getStartDate()));
        inspection.getTreatment().setEndDate(DateTimeUtils.getDateFromString(inspectionDto.getTreatment().getEndDate()));
        try {
            if (inspectionDto.getInspectionImage() != null) {
                inspection.setInspectionImage(ImageUtil.compressImage(inspectionDto.getInspectionImage().getBytes()));
            }
            if (inspectionDto.getFoodImage() != null) {
                inspection.setFoodImage(ImageUtil.compressImage(inspectionDto.getFoodImage().getBytes()));
            }
            if (inspectionDto.getPopulationImage() != null) {
                inspection.setPopulationImage(ImageUtil.compressImage(inspectionDto.getPopulationImage().getBytes()));
            }
            if (inspectionDto.getQueenImage() != null) {
                inspection.setQueenImage(ImageUtil.compressImage(inspectionDto.getQueenImage().getBytes()));
            }
            if (inspectionDto.getBroodImage() != null) {
                inspection.setBroodImage(ImageUtil.compressImage(inspectionDto.getBroodImage().getBytes()));
            }
            if (inspectionDto.getStressorsImage() != null) {
                inspection.setStressorsImage(ImageUtil.compressImage(inspectionDto.getStressorsImage().getBytes()));
            }
            if (inspectionDto.getDiseaseImage() != null) {
                inspection.setDiseaseImage(ImageUtil.compressImage(inspectionDto.getDiseaseImage().getBytes()));
            }
        } catch (IOException e) {
            log.warn("Error while get image from hive: {}", e.getMessage());
        }
        return inspection;
    }

    /**
     * Convert inspection entity to dto detail
     * @param inspection entity to convert
     * @return converted dto with inspection detail
     */
    public InspectionDetailDto convertInspectionEntity(InspectionEntity inspection) {
        return InspectionDetailDto
                .builder()
                .type(inspection.getType())
                .date(inspection.getInspectionDate().toString())
                .weather(inspection.getWeather())
                .population(inspection.getPopulation())
                .foodStorage(inspection.getFoodStorage())
                .sourceNearby(inspection.getSourceNearby())

                .broodPattern(inspection.getBroodPattern())
                .hasQueen(inspection.isQueen())
                .hasEggs(inspection.isEggs())
                .hasUncappedBrood(inspection.isUncappedBrood())
                .hasCappedBrood(inspection.isCappedBrood())

                .colonyTemperament(inspection.getTemperament())

                .stressors(convertStressorsEntity(inspection.getStressors()))
                .treatment(convertTreatmentEntity(inspection.getTreatment()))
                .feeding(convertFeedingEntity(inspection.getFeeding()))
                .harvest(convertHarvestEntity(inspection.getHarvest()))
                .notes(inspection.getNotes())
                .build();
    }

    /**
     * Convert stressors entity to dto
     * @param stressors entity to convert
     * @return converted dto of stressors entity
     */
    public StressorsDto convertStressorsEntity(StressorsEntity stressors) {
        return StressorsDto
                .builder()
                .varroaMites(stressors.isVarroaMites())
                .chalkbrood(stressors.isChalkbrood())
                .sacbrood(stressors.isSacbrood())
                .foulbrood(stressors.isFoulbrood())
                .nosema(stressors.isNosema())
                .beetles(stressors.isBeetles())
                .mice(stressors.isMice())
                .ants(stressors.isAnts())
                .moths(stressors.isMoths())
                .wasps(stressors.isWasps())
                .hornet(stressors.isHornet())
                .build();
    }

    /**
     * Convert treatment entity to dto
     * @param treatment entity to convert
     * @return converted dto of treatment entity
     */
    public TreatmentDto convertTreatmentEntity(HiveTreatmentEntity treatment) {
        return TreatmentDto
                .builder()
                .disease(treatment.getDisease())
                .treatment(treatment.getTreatment())
                .quantity(treatment.getQuantity())
                .dose(treatment.getDose())
                .startDate(treatment.getStartDate() != null ? treatment.getStartDate().toString() : "")
                .endDate(treatment.getEndDate() != null ? treatment.getEndDate().toString() : "")
                .build();
    }

    /**
     * Convert feeding entity to dto
     * @param feeding entity to convert
     * @return converted dto of feeding entity
     */
    public FeedingDto convertFeedingEntity(HiveFeedingEntity feeding) {
        return FeedingDto
                .builder()
                .food(feeding.getFood())
                .ratio(feeding.getRatio())
                .foodQuantity(feeding.getFoodQuantity())
                .foodUnit(feeding.getFoodUnit())
                .build();
    }

    /**
     * Convert harvest entity to dto
     * @param harvest entity to convert
     * @return converted dto of harvest entity
     */
    public HarvestDto convertHarvestEntity(HiveHarvestEntity harvest) {
        return HarvestDto
                .builder()
                .product(harvest.getProduct())
                .productQuantity(harvest.getProductQuantity())
                .productUnit(harvest.getProductUnit())
                .frameCount(harvest.getFrameCount())
                .superCount(harvest.getSuperCount())
                .build();
    }

    /**
     * Convert list of timeline items into list dto objects for timeline statistics
     */
    public List<GraphOverviewItem> convertObjectTimelineCounts(List<Object[]> users) {
        List<GraphOverviewItem> timeline = new ArrayList<>();
        users.forEach(user ->
                timeline.add(
                        GraphOverviewItem.builder().date(((LocalDate) user[0]).toString()).count((Long) user[1]).build()));
        return timeline;
    }

    /**
     * Convert dto data from sensors monitoring into entity record
     * @param data measured data from sensors monitoring
     * @return converted entity of measured data from sensors
     */
    public SensorsDataEntity convertSensorsDataDto(SensorDataDto data) {
        var entity =  modelMapper.map(data, SensorsDataEntity.class);
        entity.setTime(LocalDateTime.now());
        return entity;
    }

    /**
     * Convert list of entities from single hive into list of dto records
     * @param entities list of measured data
     * @return list of converted entities
     */
    public List<SensorDataDto> convertListSensorsData(List<SensorsDataEntity> entities) {
        List<SensorDataDto> timeline = new ArrayList<>();
        entities.forEach(dataRecord ->
                timeline.add(
                        SensorDataDto
                                .builder()
                                .time(DateTimeUtils.getDateTimeFromString(dataRecord.getTime()))
                                .weight(dataRecord.getWeight())
                                .hiveTemperature(dataRecord.getHiveTemperature())
                                .hiveHumidity(dataRecord.getHiveHumidity())
                                .outsideTemperature(dataRecord.getOutsideTemperature())
                                .outsideHumidity(dataRecord.getOutsideHumidity())
                                .build()));
        return timeline;
    }
}

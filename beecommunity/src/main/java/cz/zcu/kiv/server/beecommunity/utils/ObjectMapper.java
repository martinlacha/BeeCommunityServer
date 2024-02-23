package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.jpa.dto.GetUpdateUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.NewUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.NewUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public UserInfoEntity convertToUserInfoEntity(NewUserInfoDto infoDto) {
        infoDto.fillAddress();
        UserInfoEntity info =  modelMapper.map(infoDto, UserInfoEntity.class);
        LocalDate dob = DateTimeUtils.getDateFromString(infoDto.getDateOfBirth());
        info.setDateOfBirth(dob);
        return info;
    }

    public GetUpdateUserInfoDto convertUserInfoDto(UserInfoEntity userInfo) {
        GetUpdateUserInfoDto infoDto = modelMapper.map(userInfo, GetUpdateUserInfoDto.class);
        AddressEntity address = userInfo.getAddress();
        infoDto.setCountry(address.getCountry());
        infoDto.setState(address.getState());
        infoDto.setTown(address.getTown());
        infoDto.setStreet(address.getStreet());
        infoDto.setNumber(address.getNumber());
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
        return new FoundUserDto(user.getEmail(), user.getUserInfo().getName(), user.getUserInfo().getSurname());
    }

    /**
     * Convert list of post entities into list of dtos
     * @param posts to convert
     * @return list of dto
     */
    public List<CommunityPostDto> convertPostListToDtoList(List<CommunityPostEntity> posts) {
        List<CommunityPostDto> output = new ArrayList<>();
        posts.forEach(entity -> output.add(
                new CommunityPostDto(
                        entity.getId(),
                        String.format("%s %s",
                                entity.getAuthor().getUserInfo().getName(), entity.getAuthor().getUserInfo().getSurname()),
                        entity.getPost(),
                        entity.getImage(),
                        entity.getAccess(),
                        entity.getCreated().toString(),
                        convertCommentsEntityToDtoList(entity.getComments()))));
        return output;
    }

    private List<PostCommentDto> convertCommentsEntityToDtoList(List<PostCommentEntity> comments) {
        List<PostCommentDto> output = new ArrayList<>();
        comments.forEach(comment -> output.add(PostCommentDto
                .builder()
                        .id(comment.getId())
                        .author(String.format("%s %s", comment.getAuthor().getUserInfo().getName(), comment.getAuthor().getUserInfo().getSurname()))
                        .comment(comment.getComment())
                        .postId(comment.getPost().getId())
                        .date(comment.getDate().toString())
                .build()));
        return output;
    }

    /**
     * Convert dto into entity
     * @param dto to convert
     * @return entity
     */
    public CommunityPostEntity convertPostDtoToEntity(CommunityPostDto dto) {
        var entity = modelMapper.map(dto, CommunityPostEntity.class);
        if (dto.getImage() != null) {
            dto.setImage(ImageUtil.compressImage(dto.getImage()));
        } else {
            dto.setImage(new byte[0]);
        }
        return entity;
    }
}

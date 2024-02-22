package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.jpa.dto.GetUpdateUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.NewUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.NewUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.AddressEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.FriendshipEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserInfoEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ObjectMapper {

    private final ModelMapper modelMapper;

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
}

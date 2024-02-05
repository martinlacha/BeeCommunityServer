package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.NewUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.NewUserInfoDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.ResetPasswordDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.UpdateUserInfoDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<Void> createNewUser(NewUserDto user);

    ResponseEntity<Void> resetUserPassword(ResetPasswordDto resetPasswordDto);

    ResponseEntity<Void> createNewUserInfo(NewUserInfoDto userIntoDto);

    ResponseEntity<Void> updateUserInfo(UpdateUserInfoDto userInfoDto);
}

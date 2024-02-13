package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.*;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<Void> createNewUser(NewUserDto user);

    ResponseEntity<Void> resetUserPassword(ResetPasswordDto resetPasswordDto);

    ResponseEntity<Void> createNewUserInfo(NewUserInfoDto userIntoDto);

    ResponseEntity<Void> updateUserInfo(GetUpdateUserInfoDto userInfoDto);

    ResponseEntity<Void> updatePassword(UpdatePasswordDto updatePasswordDto);

    ResponseEntity<GetUpdateUserInfoDto> getUserInfo();
}

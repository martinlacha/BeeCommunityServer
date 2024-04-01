package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.user.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {
    ResponseEntity<Void> createNewUser(NewUserDto user);

    ResponseEntity<Void> resetUserPassword(String email);

    ResponseEntity<Void> createNewUserInfo(NewUserInfoDto userIntoDto);

    ResponseEntity<Void> updateUserInfo(GetUpdateUserInfoDto userInfoDto);

    ResponseEntity<Void> updatePassword(UpdatePasswordDto updatePasswordDto);

    ResponseEntity<GetUpdateUserInfoDto> getUserInfo();

    ResponseEntity<List<UserRolesDto>> getUsersRoles();

    ResponseEntity<Void> grantAdminRole(Long userId);

    ResponseEntity<Void> revokeAdminRole(Long userId);

    ResponseEntity<Void> changeUserEmail(Long userId, String newEmail);
}

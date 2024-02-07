package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.*;
import cz.zcu.kiv.server.beecommunity.services.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
@AllArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * Create a new user account
     * @param user dto with login
     * @return status code
     */
    @PostMapping("/sign-up")
    ResponseEntity<Void> createUser(@RequestBody @Valid NewUserDto user) {
        return userService.createNewUser(user);
    }

    /**
     * Endpoint to reset password
     * @param resetPasswordDto dto with email of account to reset password
     * @return Response with status code
     */
    @PostMapping("/reset-password")
    ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        return userService.resetUserPassword(resetPasswordDto);
    }

    /**
     * Endpoint to update user password
     * @param updatePasswordDto dto with email and confirm code send to mail
     * @return Response with status code
     */
    @PostMapping("/update-password")
    ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordDto updatePasswordDto) {
        return userService.updatePassword(updatePasswordDto);
    }

    /**
     * Create user info for new account
     * @param userInfoDto
     * @return
     */
    @PutMapping("/info")
    ResponseEntity<Void> createNewUserInfo(@RequestBody @Valid NewUserInfoDto userInfoDto) {
        return userService.createNewUserInfo(userInfoDto);
    }

    /**
     * Update info about user
     * @return Response with status code
     */
    @PostMapping("/info")
    ResponseEntity<Void> updateUserInfo(@RequestBody UpdateUserInfoDto userInfoDto) {
        return userService.updateUserInfo(userInfoDto);
    }
}

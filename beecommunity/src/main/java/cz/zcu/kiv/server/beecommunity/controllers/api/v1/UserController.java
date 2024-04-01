package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.user.*;
import cz.zcu.kiv.server.beecommunity.services.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for user endpoints
 */
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
     * @return created (201) if user was created, conflict (409) when email already exists otherwise bad request (400)
     */
    @PostMapping("/sign-up")
    ResponseEntity<Void> createUser(@RequestBody @Valid NewUserDto user) {
        return userService.createNewUser(user);
    }

    /**
     * Endpoint to reset password and mail will be sent
     * @param email email of account to reset password
     * @return ok (200), when user not found (404), otherwise bad request (400)
     */
    @PostMapping("/reset-password")
    ResponseEntity<Void> resetPassword(@RequestParam String email) {
        return userService.resetUserPassword(email);
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
     * @param userInfoDto object with details about user
     * @return ok (200), conflict (409) info already exists, bad request (400) some field missing
     */
    @PutMapping("/info")
    ResponseEntity<Void> createNewUserInfo(@RequestBody NewUserInfoDto userInfoDto) {
        return userService.createNewUserInfo(userInfoDto);
    }

    /**
     * Update user information
     * @return ok (200), conflict (409) user info not exists
     */
    @PostMapping("/info")
    ResponseEntity<Void> updateUserInfo(@RequestBody @Valid GetUpdateUserInfoDto userInfoDto) {
        return userService.updateUserInfo(userInfoDto);
    }

    /**
     * Find a return user info
     * @return user info
     */
    @GetMapping("/info")
    ResponseEntity<GetUpdateUserInfoDto> getUserInfo() {
        return userService.getUserInfo();
    }

    /**
     * Return list of users with roles assigned to them
     * @return users with roles
     */
    @GetMapping("/roles-info")
    ResponseEntity<List<UserRolesDto>> getUsersRoles() {
        return userService.getUsersRoles();
    }

    /**
     * Grant user with admin role
     * @return status code of operation result
     */
    @PostMapping("/admin")
    ResponseEntity<Void> grantAdminRole(@RequestParam Long userId) {
        return userService.grantAdminRole(userId);
    }

    /**
     * Revoke admin role from user
     * @return status code of operation result
     */
    @DeleteMapping("/admin")
    ResponseEntity<Void> revokeAdminRole(@RequestParam Long userId) {
        return userService.revokeAdminRole(userId);
    }

    /**
     * Change user email
     * @param userId user id which will be changed email
     * @param newEmail new email of user
     * @return status code of operation result
     */
    @PostMapping("/email")
    ResponseEntity<Void> changeUserEmail(@RequestParam Long userId, @RequestParam String newEmail) {
        return userService.changeUserEmail(userId, newEmail);
    }
}

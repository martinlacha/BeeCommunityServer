package cz.zcu.kiv.server.beecommunity.jpa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Dto to update password of user
 * Code is sent on email of user to authenticate
 */
@Data
public class UpdatePasswordDto {
    @Email(message = "Not valid email address")
    @NotEmpty(message = "Name can't be empty.")
    @NotBlank(message = "Name can't be blank.")
    String email;

    @NotEmpty(message = "Name can't be empty.")
    @NotBlank(message = "Name can't be blank.")
    String code;

    @NotEmpty(message = "Name can't be empty.")
    @NotBlank(message = "Name can't be blank.")
    @Size(min = 8, message = "Password has to be at least 8 chars")
    String newPassword;
}

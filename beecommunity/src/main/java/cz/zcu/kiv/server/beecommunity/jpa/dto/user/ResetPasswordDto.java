package cz.zcu.kiv.server.beecommunity.jpa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Dto to reset user password by email address
 */
@Data
public class ResetPasswordDto {
    @NotEmpty(message = "Email can't be empty.")
    @Email(message = "Not valid email address.")
    private String email;
}

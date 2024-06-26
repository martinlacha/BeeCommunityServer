package cz.zcu.kiv.server.beecommunity.jpa.dto.user;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for register new user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotEmpty(message = "Email can't be empty.")
    @Email(message = "Not valid email address.")
    private String email;

    @NotEmpty(message = "Password can't be empty.")
    @NotBlank(message = "Password can't be blank.")
    @Size(min = 10, message = "Length of password has to be at least 10 characters.")
    private String password;
}

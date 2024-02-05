package cz.zcu.kiv.server.beecommunity.jpa.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NewUserDto {

    @NotEmpty(message = "Email can't be empty.")
    @Email(message = "Not valid email address.")
    private String email;

    @NotEmpty(message = "Password can't be empty.")
    @NotBlank(message = "Password can't be blank.")
    @Size(min = 10, message = "Lenght of password has to be at least 10 characters.")
    private String password;
}

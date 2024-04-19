package cz.zcu.kiv.server.beecommunity.jpa.dto.user;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto with information about new user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserInfoDto {
    @NotEmpty(message = "Name can't be empty.")
    @NotBlank(message = "Name can't be blank.")
    @Size(min = 1, max = 50, message = "Lenght of name has to be 1 - 50 characters.")
    private String name;

    @NotEmpty(message = "Surname can't be empty.")
    @NotBlank(message = "Surname can't be blank.")
    @Size(min = 1, max = 50, message = "Lenght of surname has to be 1 - 50 characters.")
    private String surname;

    @NotNull(message = "Surname can't be empty.")
    private String dateOfBirth;

    @NotNull(message = "Experience can't be empty.")
    private UserEnums.EExperience experience;

    @NotBlank(message = "Name can't be empty.")
    @NotEmpty(message = "Name can't be blank.")
    @Size(min = 1, max = 50, message = "Lenght of name has to be 1 - 50 characters.")
    private String state;

    @NotEmpty(message = "Name can't be empty.")
    @NotBlank(message = "Name can't be blank.")
    @Size(min = 1, max = 50, message = "Lenght of name has to be 1 - 50 characters.")
    private String country;

    @NotEmpty(message = "Name can't be empty.")
    @NotBlank(message = "Name can't be blank.")
    @Size(min = 1, max = 50, message = "Lenght of name has to be 1 - 50 characters.")
    private String town;

    private String street;

    @NotNull(message = "Name can't be empty.")
    @Positive(message = "Number can't be negative or zero.")
    private int number;

    private AddressDto address;

    /**
     * Method to fill address dto for correct object mapper
     */
    public void fillAddress() {
        address = new AddressDto();
        address.setState(state);
        address.setCountry(country);
        address.setTown(town);
        address.setStreet(street);
        address.setNumber(number);
    }
}

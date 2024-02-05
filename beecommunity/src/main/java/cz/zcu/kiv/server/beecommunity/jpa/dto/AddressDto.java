package cz.zcu.kiv.server.beecommunity.jpa.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String state;
    private String country;
    private String town;
    private String zip;
    private String street;
    private int number;
}

package cz.zcu.kiv.server.beecommunity.jpa.dto.apiary;

import cz.zcu.kiv.server.beecommunity.enums.ApiaryEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dto for apiary
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiaryDto {
    private Long id;

    private String name;

    private ApiaryEnums.EEnvironment environment;

    private ApiaryEnums.ETerrain terrain;

    private MultipartFile image;

    private String latitude;

    private String longitude;

    private String notes;
}

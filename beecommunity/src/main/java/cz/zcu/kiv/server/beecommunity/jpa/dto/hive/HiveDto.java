package cz.zcu.kiv.server.beecommunity.jpa.dto.hive;

import cz.zcu.kiv.server.beecommunity.enums.HiveEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dto for hive
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HiveDto {
    private Long id;

    private String name;

    private Long apiaryId;

    private MultipartFile image;

    private String establishment;

    private String notes;

    private HiveEnums.EBeeSource source;

    private HiveEnums.EColor color;
}

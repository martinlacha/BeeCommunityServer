package cz.zcu.kiv.server.beecommunity.jpa.dto.community;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Dto for create or update
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPostDto {
    private Long id;

    private String author;

    @NotEmpty(message = "Post can't be empty")
    @NotBlank(message = "Post can't be blank")
    private String post;

    private byte[] image;

    @NotNull(message = "Experience can't be empty.")
    private CommunityEnums.EAccess access;

    private String date;

    List<PostCommentDto> comments;
}

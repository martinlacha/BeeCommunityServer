package cz.zcu.kiv.server.beecommunity.jpa.dto.community;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

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
    @Length(max = 50, message = "Title has to be up to 50 characters")
    private String title;

    @NotEmpty(message = "Post can't be empty")
    @NotBlank(message = "Post can't be blank")
    private String post;

    private MultipartFile image;

    @NotNull(message = "Experience can't be empty.")
    private CommunityEnums.EAccess access;

    private String date;

    List<PostCommentDto> comments;
}

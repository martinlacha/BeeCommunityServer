package cz.zcu.kiv.server.beecommunity.jpa.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentDto {

    private Long id;

    private String author;

    @NotNull(message = "Post can't be null")
    private Long postId;

    @NotEmpty(message = "Comment can't be empty")
    @NotBlank(message = "Comment can't be blank")
    private String comment;

    private String date;
}

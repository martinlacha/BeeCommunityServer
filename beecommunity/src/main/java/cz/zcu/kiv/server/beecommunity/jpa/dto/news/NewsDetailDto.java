package cz.zcu.kiv.server.beecommunity.jpa.dto.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsDetailDto {
    Long id;

    @NotEmpty(message = "Title can't be empty")
    @NotBlank(message = "Title can't be blank")
    @Length(max = 50, message = "Title has to be up to 50 characters")
    String title;

    @NotEmpty(message = "Article text can't be empty")
    @NotBlank(message = "Article text can't be blank")
    String article;

    MultipartFile titleImage;

    MultipartFile firstImage;

    MultipartFile secondImage;

    String author;
}

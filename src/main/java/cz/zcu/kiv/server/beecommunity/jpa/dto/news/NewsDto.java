
package cz.zcu.kiv.server.beecommunity.jpa.dto.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for news
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    Long id;

    String title;

    String article;

    String author;

    String date;
}

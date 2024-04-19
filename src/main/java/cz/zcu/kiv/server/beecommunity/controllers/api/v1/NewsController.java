package cz.zcu.kiv.server.beecommunity.controllers.api.v1;


import cz.zcu.kiv.server.beecommunity.enums.NewsEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDto;
import cz.zcu.kiv.server.beecommunity.services.INewsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for news endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/news")
@Tag(name = "News")
@AllArgsConstructor
public class NewsController {
    private final INewsService newsService;

    /**
     * Get list of articles dto with information
     * @return list of articles
     */
    @GetMapping
    ResponseEntity<List<NewsDto>> getArticles() {
        return newsService.getNews();
    }

    /**
     * Create new article
     * @param newsDetailDto object contain information about article
     * @return created article as dto
     */
    @PostMapping
    ResponseEntity<Void> createArticle(@ModelAttribute @Valid NewsDetailDto newsDetailDto) {
        return newsService.createArticle(newsDetailDto);
    }

    /**
     * Update article title or information
     * @param newsDetailDto dto with information to update
     * @return updated dto
     */
    @PutMapping
    ResponseEntity<Void> updateArticle(@ModelAttribute @Valid NewsDetailDto newsDetailDto) {
        return newsService.updateArticle(newsDetailDto);
    }

    /**
     * Delete article by its id
     * @param articleId id of article
     * @return status code of operation
     */
    @DeleteMapping("/detail")
    ResponseEntity<Void> deleteArticle(@RequestParam Long articleId) {
        return newsService.deleteArticle(articleId);
    }

    /**
     * Get news details with text and images
     * @param articleId article id
     * @return article details (images, text, author)
     */
    @GetMapping("/detail")
    ResponseEntity<NewsDetailDto> getArticleDetail(@RequestParam Long articleId) {
        return newsService.getArticleDetail(articleId);
    }

    /**
     * Return article image by articleId and image type
     * @param articleId article id
     * @param image type
     * @return byte array of decompressed image
     */
    @GetMapping("/image")
    ResponseEntity<byte[]> getArticleImage(@RequestParam Long articleId, @RequestParam NewsEnums.EImage image) {
        return newsService.getArticleImage(articleId, image);
    }
}

package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface INewsService {
    ResponseEntity<List<NewsDto>> getNews();
    ResponseEntity<NewsDetailDto> createArticle(NewsDetailDto newsDetailDto);
    ResponseEntity<Void> deleteArticle(Long articleId);
    ResponseEntity<NewsDetailDto> updateArticle(NewsDetailDto newsDetailDto);
    ResponseEntity<NewsDetailDto> getArticleDetail(Long articleId);

}

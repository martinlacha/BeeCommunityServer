package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.enums.NewsEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface INewsService {
    ResponseEntity<List<NewsDto>> getNews();
    ResponseEntity<Void> createArticle(NewsDetailDto newsDetailDto);
    ResponseEntity<Void> deleteArticle(Long articleId);
    ResponseEntity<Void> updateArticle(NewsDetailDto newsDetailDto);
    ResponseEntity<NewsDetailDto> getArticleDetail(Long articleId);
    ResponseEntity<byte[]> getArticleImage(Long articleId, NewsEnums.EImage image);
}

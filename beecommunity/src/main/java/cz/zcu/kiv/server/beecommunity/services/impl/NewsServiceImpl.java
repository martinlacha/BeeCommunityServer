package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDto;
import cz.zcu.kiv.server.beecommunity.jpa.repository.NewsRepository;
import cz.zcu.kiv.server.beecommunity.services.INewsService;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements INewsService {
    private final NewsRepository newsRepository;

    private final ObjectMapper modelMapper;

    /**
     * Find and return response with list of news
     * @return list of news
     */
    @Override
    public ResponseEntity<List<NewsDto>> getNews() {
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertNewsList(newsRepository.findAll()));
    }

    /**
     * Create new article
     * @param newsDetailDto dto with new article
     * @return dto of article
     */
    @Override
    public ResponseEntity<NewsDetailDto> createArticle(NewsDetailDto newsDetailDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var article = modelMapper.convertNewsDtoToEntity(newsDetailDto);
        article.setAuthor(user);
        newsRepository.saveAndFlush(article);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Delete article by its id
     * @param articleId article id
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> deleteArticle(Long articleId) {
        var article = newsRepository.findById(articleId);
        if (article.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        newsRepository.delete(article.get());
        newsRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Update existing article
     * @param newsDetailDto dto of article which will be updated
     * @return updated article
     */
    @Override
    public ResponseEntity<NewsDetailDto> updateArticle(NewsDetailDto newsDetailDto) {
        var article = newsRepository.findById(newsDetailDto.getId());
        if (article.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (newsDetailDto.getTitle() != null) {
            article.get().setTitle(newsDetailDto.getTitle());
        } else if (newsDetailDto.getArticle() != null) {
            article.get().setArticle(newsDetailDto.getArticle());
        } else if (newsDetailDto.getFirstImage() != null) {
            article.get().setFirstImage(ImageUtil.compressImage(newsDetailDto.getFirstImage()));
        } else if (newsDetailDto.getSecondImage() != null) {
            article.get().setSecondImage(ImageUtil.compressImage(newsDetailDto.getSecondImage()));
        }
        newsRepository.saveAndFlush(article.get());
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertNewsEntityToDto(article.get()));
    }

    /**
     * Find article by id and return details
     * @param articleId article id
     * @return dto with article details
     */
    @Override
    public ResponseEntity<NewsDetailDto> getArticleDetail(Long articleId) {
        return newsRepository.findById(articleId).map(newsEntity ->
                ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertNewsEntityToDto(newsEntity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

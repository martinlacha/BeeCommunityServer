package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.NewsEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDetailDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.news.NewsDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.NewsEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserInfoEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.NewsRepository;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NewsServiceImpl newsService;

    private final TestData data = new TestData();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        UserEntity user = UserEntity
                .builder()
                .id(1L)
                .userInfo(UserInfoEntity.builder().build())
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void testGetNews_Success() {
        List<NewsEntity> input = List.of(data.getNews1(), data.getNews2());
        List<NewsDto> expected  = List.of(data.getNewsDto1(), data.getNewsDto2());
        when(newsRepository.findAll()).thenReturn(input);
        when(objectMapper.convertNewsList(anyList())).thenReturn(expected);
        ResponseEntity<List<NewsDto>> response = newsService.getNews();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.size(), input.size());
        assertEquals(expected.get(0).getId(), input.get(0).getId());
        assertEquals(expected.get(0).getTitle(), input.get(0).getTitle());
        assertEquals(expected.get(0).getArticle(), input.get(0).getArticle());
        assertEquals(expected.get(0).getDate(), input.get(0).getDate().toString());
        assertEquals(expected.get(0).getAuthor(), input.get(0).getAuthor().getFullName());

        verify(newsRepository).findAll();
        verify(objectMapper).convertNewsList(input);
        verifyNoMoreInteractions(newsRepository, objectMapper);
    }

    @Test
    void testCreateArticle_Success() {
        NewsDetailDto detail = data.getNewsDetailDto1();
        when(objectMapper.convertNewsDtoToEntity(detail)).thenReturn(data.getNews1());
        ResponseEntity<Void> response = newsService.createArticle(detail);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(objectMapper).convertNewsDtoToEntity(detail);
        verify(newsRepository).saveAndFlush(any(NewsEntity.class));
        verifyNoMoreInteractions(objectMapper, newsRepository);
    }

    @Test
    void testDeleteArticle_Success() {
        NewsEntity news = data.getNews1();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));
        ResponseEntity<Void> response = newsService.deleteArticle(news.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(newsRepository).findById(news.getId());
        verify(newsRepository).delete(news);
        verify(newsRepository).flush();
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void testDeleteArticle_NotFound() {
        NewsEntity news = data.getNews1();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.empty());
        ResponseEntity<Void> response = newsService.deleteArticle(news.getId());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(newsRepository).findById(news.getId());
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void testUpdateArticle_Success() {
        NewsEntity news = data.getNews2();
        NewsDetailDto updatedArticleDto = data.getNewsDetailDto2();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));
        when(newsRepository.saveAndFlush(news)).thenReturn(news);
        ResponseEntity<Void> response = newsService.updateArticle(updatedArticleDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(newsRepository).findById(news.getId());
        verify(newsRepository).saveAndFlush(news);
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void testUpdateArticle_NotFound() throws IOException {
        NewsEntity news = data.getNews2();
        NewsDetailDto updatedArticleDto = data.getNewsDetailDto2();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.empty());
        ResponseEntity<Void> response = newsService.updateArticle(updatedArticleDto);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(newsRepository).findById(news.getId());
        verifyNoMoreInteractions(newsRepository);
    }


    @Test
    void testGetArticleDetail_Success() {
        NewsEntity news = data.getNews2();
        NewsDetailDto articleDto = data.getNewsDetailDto2();
        when(newsRepository.findById(2L)).thenReturn(Optional.of(news));
        when(objectMapper.convertNewsEntityToDto(news)).thenReturn(data.getNewsDetailDto2());
        ResponseEntity<NewsDetailDto> response = newsService.getArticleDetail(news.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(articleDto, response.getBody());
        verify(newsRepository).findById(news.getId());
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void testGetArticleDetail_NotFound() {
        NewsEntity news = data.getNews2();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.empty());
        when(objectMapper.convertNewsEntityToDto(news)).thenReturn(data.getNewsDetailDto2());
        ResponseEntity<NewsDetailDto> response = newsService.getArticleDetail(news.getId());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(newsRepository).findById(news.getId());
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void testGetArticleImage_TitleImage_Success() {
        try (MockedStatic<ImageUtil> utilities = Mockito.mockStatic(ImageUtil.class)) {
            NewsEntity news = data.getNews3();
            utilities.when(() -> ImageUtil.decompressImage(any())).thenReturn(news.getTitleImage());
            when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));
            when(ImageUtil.decompressImage(any())).thenReturn(news.getTitleImage());
            ResponseEntity<byte[]> response = newsService.getArticleImage(news.getId(), NewsEnums.EImage.TITLE);
            var image = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(image);
            assertEquals(news.getTitleImage().length, image.length);
            assertArrayEquals(news.getTitleImage(), response.getBody());
            verify(newsRepository).findById(news.getId());
            verifyNoMoreInteractions(newsRepository);
        }
    }

    @Test
    void testGetArticleImage_TitleImage_NotFound() {
        NewsEntity news = data.getNews2();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.empty());
        ResponseEntity<byte[]> response = newsService.getArticleImage(news.getId(), NewsEnums.EImage.TITLE);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(newsRepository).findById(news.getId());
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void testGetArticleImage_FirstImage_Success() {
        try (MockedStatic<ImageUtil> utilities = Mockito.mockStatic(ImageUtil.class)) {
            NewsEntity news = data.getNews3();
            utilities.when(() -> ImageUtil.decompressImage(any())).thenReturn(news.getFirstImage());
            when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));
            when(ImageUtil.decompressImage(any())).thenReturn(news.getFirstImage());
            ResponseEntity<byte[]> response = newsService.getArticleImage(news.getId(), NewsEnums.EImage.FIRST);
            var image = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(image);
            assertEquals(news.getFirstImage().length, image.length);
            assertArrayEquals(news.getFirstImage(), response.getBody());
            verify(newsRepository).findById(news.getId());
            verifyNoMoreInteractions(newsRepository);
        }
    }

    @Test
    void testGetArticleImage_FirstImage_NotFound() {
        NewsEntity news = data.getNews1();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.empty());
        ResponseEntity<byte[]> response = newsService.getArticleImage(news.getId(), NewsEnums.EImage.FIRST);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(newsRepository).findById(news.getId());
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void testGetArticleImage_SecondImage_Success() {
        try (MockedStatic<ImageUtil> utilities = Mockito.mockStatic(ImageUtil.class)) {
            NewsEntity news = data.getNews3();
            utilities.when(() -> ImageUtil.decompressImage(any())).thenReturn(news.getSecondImage());
            when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));
            when(ImageUtil.decompressImage(any())).thenReturn(news.getSecondImage());
            ResponseEntity<byte[]> response = newsService.getArticleImage(news.getId(), NewsEnums.EImage.SECOND);
            var image = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(image);
            assertEquals(news.getSecondImage().length, image.length);
            assertArrayEquals(news.getSecondImage(), response.getBody());
            verify(newsRepository).findById(news.getId());
            verifyNoMoreInteractions(newsRepository);
        }
    }

    @Test
    void testGetArticleImage_SecondImage_NotFound() {
        NewsEntity news = data.getNews2();
        when(newsRepository.findById(news.getId())).thenReturn(Optional.empty());
        ResponseEntity<byte[]> response = newsService.getArticleImage(news.getId(), NewsEnums.EImage.SECOND);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(newsRepository).findById(news.getId());
        verifyNoMoreInteractions(newsRepository);
    }
}

package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.CommunityPostEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.PostCommentEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.CommunityPostRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.PostCommentRepository;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommunityPostServiceImplTest {

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private FriendshipUtils friendshipUtils;

    @Mock
    private UserUtils userUtils;

    @Mock
    private PostCommentRepository commentRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private ObjectMapper modelMapper;

    @InjectMocks
    private CommunityPostServiceImpl communityPostService;

    private UserEntity user;

    private final TestData testData = new TestData();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        user = testData.getUser1();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void testCreatePost_Success() {
        CommunityPostDto postDto = testData.getPostDto();
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(modelMapper.convertPostDtoToEntity(postDto)).thenReturn(testData.getPost1());

        ResponseEntity<Void> response = communityPostService.createPost(postDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(communityPostRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testGetPost_Success() {
        CommunityPostDto postDto = testData.getPostDto();
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(communityPostRepository.findById(eq(postDto.getId()))).thenReturn(Optional.empty());

        ResponseEntity<List<CommunityPostDto>> response = communityPostService.getPosts(CommunityEnums.EAccess.PUBLIC);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(communityPostRepository, times(1)).findByAccessOrderById(any());
        verify(modelMapper, times(1)).convertPostListToDtoList(any());
    }

    @Test
    void testGetPostDetail_NotFound() {
        CommunityPostDto postDto = testData.getPostDto();
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(communityPostRepository.findById(eq(postDto.getId()))).thenReturn(Optional.empty());

        ResponseEntity<CommunityPostDto> response = communityPostService.getPostDetail(postDto.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(modelMapper);
        verify(communityPostRepository, times(1)).findById(eq(postDto.getId()));
    }

    @Test
    void testGetPostDetail_BadRequest() {
        CommunityPostDto postDto = testData.getPostDto();
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(communityPostRepository.findById(eq(postDto.getId()))).thenReturn(Optional.ofNullable(testData.getPost2()));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);


        ResponseEntity<CommunityPostDto> response = communityPostService.getPostDetail(postDto.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(modelMapper);
        verify(communityPostRepository, times(1)).findById(eq(postDto.getId()));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
    }

    @Test
    void testGetPostDetail_Success_MyPost() {
        CommunityPostDto postDto = testData.getPostDto();
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(communityPostRepository.findById(eq(postDto.getId()))).thenReturn(Optional.ofNullable(testData.getPost1()));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);


        ResponseEntity<CommunityPostDto> response = communityPostService.getPostDetail(postDto.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(modelMapper, times(1)).convertPostEntityToDto(any());
        verify(communityPostRepository, times(1)).findById(eq(postDto.getId()));
    }

    @Test
    void testGetPostDetail_Success_FriendPost() {
        CommunityPostDto postDto = testData.getPostDto();
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(communityPostRepository.findById(eq(postDto.getId()))).thenReturn(Optional.ofNullable(testData.getPost2()));
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);


        ResponseEntity<CommunityPostDto> response = communityPostService.getPostDetail(postDto.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(modelMapper, times(1)).convertPostEntityToDto(any());
        verify(communityPostRepository, times(1)).findById(eq(postDto.getId()));
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
    }

    @Test
    void testGetPostImage_NotFound() {
        when(communityPostRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = communityPostService.getPostImage(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetPostImage_NoImage() {
        long postId = 1L;
        CommunityPostEntity postEntity = new CommunityPostEntity();
        postEntity.setId(postId);
        postEntity.setImage(null);
        when(communityPostRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        ResponseEntity<byte[]> response = communityPostService.getPostImage(postId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testGetPostImage_CantSee() {
        CommunityPostEntity postEntity = testData.getPost2();
        postEntity.setImage("Image".getBytes());
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);
        when(communityPostRepository.findById(2L)).thenReturn(Optional.of(postEntity));

        ResponseEntity<byte[]> response = communityPostService.getPostImage(2L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
    }

    @Test
    void testGetPostImage_CanSeePost() {
        CommunityPostEntity postEntity = testData.getPost2();
        postEntity.setImage("Image".getBytes());
        when(friendshipUtils.isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);
        when(communityPostRepository.findById(2L)).thenReturn(Optional.of(postEntity));

        ResponseEntity<byte[]> response = communityPostService.getPostImage(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
        verify(friendshipUtils, times(1)).isFriendshipStatus(any(), any(), eq(FriendshipEnums.EStatus.FRIEND));
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(communityPostRepository.findById(eq(1L))).thenReturn(Optional.empty());

        ResponseEntity<Void> response = communityPostService.deletePost(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
    }

    @Test
    void testDeletePost_UserNotAuthor() {
        CommunityPostEntity postEntity = testData.getPost3();
        when(communityPostRepository.findById(eq(postEntity.getId()))).thenReturn(Optional.of(postEntity));

        ResponseEntity<Void> response = communityPostService.deletePost(postEntity.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
        verify(communityPostRepository, never()).deleteById(eq(postEntity.getId()));
        verify(communityPostRepository, never()).flush();
    }

    @Test
    void testDeletePost_Success() {
        CommunityPostEntity postEntity = testData.getPost4();
        when(communityPostRepository.findById(postEntity.getId())).thenReturn(Optional.of(postEntity));

        ResponseEntity<Void> response = communityPostService.deletePost(postEntity.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
        verify(communityPostRepository, times(1)).deleteById(eq(postEntity.getId()));
        verify(communityPostRepository, times(1)).flush();
    }

    @Test
    void testUpdatePost_NullId() {
        CommunityPostDto postDto = testData.getPostDto();
        postDto.setId(null);

        ResponseEntity<Void> response = communityPostService.updatePost(postDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(communityPostRepository);
    }

    @Test
    void testUpdatePost_PostNotFound() {
        CommunityPostDto postDto = testData.getPostDto();
        when(communityPostRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = communityPostService.updatePost(postDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
    }

    @Test
    void testUpdatePost_UserNotAuthor() {
        CommunityPostEntity postEntity = testData.getPost1();
        when(communityPostRepository.findById(any())).thenReturn(Optional.of(postEntity));

        ResponseEntity<Void> response = communityPostService.updatePost(CommunityPostDto.builder().build());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoMoreInteractions(communityPostRepository);
    }

    @Test
    void testUpdatePost_Success() {
        CommunityPostDto postDto = testData.getPostDto();
        CommunityPostEntity postEntity = testData.getPost1();
        when(communityPostRepository.findById(any())).thenReturn(Optional.of(postEntity));

        ResponseEntity<Void> response = communityPostService.updatePost(postDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
        verify(communityPostRepository, times(1)).saveAndFlush(postEntity);
    }

    @Test
    void testAddComment_PostNotFound() {
        when(communityPostRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<PostCommentDto> response = communityPostService.addComment(PostCommentDto.builder().build());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
        verifyNoInteractions(commentRepository);
    }

    @Test
    void testAddComment_UnauthorizedUser() {
        CommunityPostEntity postEntity = testData.getPost3();
        PostCommentDto commentDto = PostCommentDto.builder().build();
        when(communityPostRepository.findById(any())).thenReturn(Optional.of(postEntity));

        ResponseEntity<PostCommentDto> response = communityPostService.addComment(commentDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
        verifyNoInteractions(commentRepository);
    }

    @Test
    void testAddComment_Success() {
        CommunityPostEntity postEntity = testData.getPost1();
        PostCommentDto commentDto = PostCommentDto.builder().build();
        when(communityPostRepository.findById(any())).thenReturn(Optional.of(postEntity));
        when(commentRepository.saveAndFlush(any(PostCommentEntity.class))).thenAnswer(invocation -> {
            PostCommentEntity commentEntity = invocation.getArgument(0);
            commentEntity.setId(1L);
            return commentEntity;
        });

        ResponseEntity<PostCommentDto> response = communityPostService.addComment(commentDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(communityPostRepository, times(1)).findById(any());
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        when(commentRepository.findById(eq(1L))).thenReturn(Optional.empty());

        ResponseEntity<Void> response = communityPostService.deleteComment(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(commentRepository, times(1)).findById(any());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void testDeleteComment_UnauthorizedUser() {
        PostCommentEntity commentEntity = PostCommentEntity.builder().author(testData.getUser2()).build();
        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));

        ResponseEntity<Void> response = communityPostService.deleteComment(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(commentRepository, times(1)).findById(any());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void testDeleteComment_Success() {
        PostCommentEntity commentEntity = PostCommentEntity.builder().author(user).build();
        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));

        ResponseEntity<Void> response = communityPostService.deleteComment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentRepository, times(1)).deleteById(eq(1L));
        verify(commentRepository, times(1)).flush();
    }
}

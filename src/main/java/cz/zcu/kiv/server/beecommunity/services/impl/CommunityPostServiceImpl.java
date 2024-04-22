package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.PostCommentEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.CommunityPostRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.PostCommentRepository;
import cz.zcu.kiv.server.beecommunity.services.ICommunityPostService;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements ICommunityPostService {
    private final CommunityPostRepository communityPostRepository;

    private final PostCommentRepository commentRepository;

    private final ObjectMapper modelMapper;

    private final FriendshipUtils friendshipUtils;

    /**
     * Create new post
     * @param postDto dto of new post
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> createPost(CommunityPostDto postDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var postEntity = modelMapper.convertPostDtoToEntity(postDto);
        postEntity.setAuthor(user);
        postEntity.setCreated(LocalDate.now());
        communityPostRepository.saveAndFlush(postEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Return list of posts
     * @param access return posts by type
     * @return list of posts
     */
    @Override
    public ResponseEntity<List<CommunityPostDto>> getPosts(CommunityEnums.EAccess access) {
        var user = UserUtils.getUserFromSecurityContext();
        var posts = communityPostRepository.findByAccessOrderById(access);
        if (CommunityEnums.EAccess.PRIVATE.equals(access)) {
            posts = posts.stream().filter(post -> canSeePost(user.getId(), post.getAuthor().getId())).toList();
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertPostListToDtoList(posts));
    }

    /**
     * Get detail of post
     * @param postId id of post
     * @return dto with details of post
     */
    @Override
    public ResponseEntity<CommunityPostDto> getPostDetail(Long postId) {
        var user = UserUtils.getUserFromSecurityContext();
        var post = communityPostRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (CommunityEnums.EAccess.PUBLIC.equals(post.get().getAccess()) ||
                canSeePost(user.getId(), post.get().getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertPostEntityToDto(post.get()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Return image as byte array from database if exist on post
     * @param postId post id
     * @return byte array of image
     */
    @Override
    public ResponseEntity<byte[]> getPostImage(Long postId) {
        var user = UserUtils.getUserFromSecurityContext();
        var post = communityPostRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (post.get().getImage() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else if (canSeePost(user.getId(), post.get().getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.OK).body(ImageUtil.decompressImage(post.get().getImage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    /**
     * Delete post by id
     * @param postId id of post
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> deletePost(Long postId) {
        var user = UserUtils.getUserFromSecurityContext();
        var post = communityPostRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(post.get().getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        communityPostRepository.deleteById(postId);
        communityPostRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Update comminity post. Changes can do only author
     * @param postDto dto of post which will be updated
     * @return status code of operation
     */
    @Override
    public ResponseEntity<Void> updatePost(CommunityPostDto postDto) {
        var user = UserUtils.getUserFromSecurityContext();
        if (postDto.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var post = communityPostRepository.findById(postDto.getId());
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(post.get().getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (postDto.getImage() != null) {
                post.get().setImage(ImageUtil.compressImage(postDto.getImage().getBytes()));
            }
        } catch (IOException e) {
            log.error("Error while get bytes from update post image: {}", e.getMessage());
        }

        post.get().setPost(postDto.getPost());
        post.get().setAccess(postDto.getAccess());
        communityPostRepository.saveAndFlush(post.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Add comment to post
     * @param commentDto dto object with comment info
     * @return status code of operation
     */
    @Override
    public ResponseEntity<PostCommentDto> addComment(PostCommentDto commentDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var post = communityPostRepository.findById(commentDto.getPostId());
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (isPrivateCommentInvalid(user.getId(), post.get().getAuthor().getId()) &&
                !user.getId().equals(post.get().getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var newComment = PostCommentEntity
                .builder()
                .author(user)
                .post(post.get())
                .comment(commentDto.getComment())
                .date(LocalDate.now())
                .build();
        commentRepository.saveAndFlush(newComment);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertCommentToDto(newComment));
    }

    /**
     * Delete comment from post
     * @param commentId if of comment that will be deleted
     * @return status code of operation
     */
    @Override
    public ResponseEntity<Void> deleteComment(Long commentId) {
        var user = UserUtils.getUserFromSecurityContext();
        Optional<PostCommentEntity> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!comment.get().getAuthor().getEmail().equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        commentRepository.deleteById(commentId);
        commentRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Check if two user are friends
     * @param user id of first user
     * @param author id of second user
     * @return true if are friends, otherwise false
     */
    private boolean isPrivateCommentInvalid(Long user, Long author) {
        return !friendshipUtils.isFriendshipStatus(user, author, FriendshipEnums.EStatus.FRIEND);
    }

    /**
     * Check if user can see post
     * Users has to be friends, or it is same user
     * @param userId first user id
     * @param authorId id author of post
     * @return true if user can see post, otherwise false
     */
    private boolean canSeePost(Long userId, Long authorId) {
        return friendshipUtils.isFriendshipStatus(userId, authorId, FriendshipEnums.EStatus.FRIEND) ||
                userId.equals(authorId);
    }
}

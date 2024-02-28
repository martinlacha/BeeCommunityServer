package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import cz.zcu.kiv.server.beecommunity.services.ICommunityPostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/community-post")
@Tag(name = "Community post")
@AllArgsConstructor
public class CommunityPostController {
    private final ICommunityPostService communityPostService;

    /**
     * Return private posts
     * @return list of private posts
     */
    @GetMapping("/private")
    ResponseEntity<List<CommunityPostDto>> getPrivatePosts() {
        return communityPostService.getPosts(CommunityEnums.EAccess.PRIVATE);
    }

    /**
     * Return public posts
     * @return list of public posts
     */
    @GetMapping("/public")
    ResponseEntity<List<CommunityPostDto>> getPublicPosts() {
        return communityPostService.getPosts(CommunityEnums.EAccess.PUBLIC);
    }

    /**
     * Create new post with title, text and image
     * @param postDto information about post
     * @return status code of operation result
     */
    @PostMapping
    ResponseEntity<Void> createPost(@ModelAttribute @Valid CommunityPostDto postDto) {
        return communityPostService.createPost(postDto);
    }

    /**
     * Find post and return detail by post id
     * @param postId post id
     * @return dto with post details
     */
    @GetMapping("/detail")
    ResponseEntity<CommunityPostDto> getPostDetail(@RequestParam Long postId) {
        return communityPostService.getPostDetail(postId);
    }

    /**
     * Get post image if was uploaded
     * @param postId post id
     * @return byte array of image
     */
    @GetMapping("/image")
    ResponseEntity<byte[]> getPostImage(@RequestParam Long postId) {
        return communityPostService.getPostImage(postId);
    }

    /**
     * Delete post by id. User has to be owner of this post
     * @param postId post id
     * @return status code of operation result
     */
    @DeleteMapping
    ResponseEntity<Void> deletePost(@RequestParam Long postId) {
        return communityPostService.deletePost(postId);
    }

    /**
     * Update post. User has to be owner of this post
     * @param postDto updated post
     * @return status code of operation result
     */
    @PutMapping
    ResponseEntity<Void> updatePost(@ModelAttribute @Valid CommunityPostDto postDto) {
        return communityPostService.updatePost(postDto);
    }

    /**
     * Create comment under the post.
     * Post has to be public or user and owner of post are friends
     * @param commentDto new comment
     * @return comment dto
     */
    @PostMapping("/comment")
    ResponseEntity<PostCommentDto> addPostComment(@RequestBody @Valid PostCommentDto commentDto) {
        return communityPostService.addComment(commentDto);
    }

    /**
     * Delete comment by id. User has to be owner of comment
     * @param commentId comment id
     * @return status code of operation result
     */
    @DeleteMapping("/comment")
    ResponseEntity<Void> deletePostComment(@RequestParam Long commentId) {
        return communityPostService.deleteComment(commentId);
    }
}

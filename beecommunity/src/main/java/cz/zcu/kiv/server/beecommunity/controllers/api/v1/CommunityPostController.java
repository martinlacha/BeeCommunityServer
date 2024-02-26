package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import cz.zcu.kiv.server.beecommunity.services.ICommunityPostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/private")
    ResponseEntity<List<CommunityPostDto>> getPrivatePosts() {
        return communityPostService.getPosts(CommunityEnums.EAccess.PRIVATE);
    }

    @GetMapping("/public")
    ResponseEntity<List<CommunityPostDto>> getPublicPosts() {
        return communityPostService.getPosts(CommunityEnums.EAccess.PUBLIC);
    }

    @PostMapping
    ResponseEntity<Void> createPost(@RequestBody @Valid CommunityPostDto postDto) {
        return communityPostService.createPost(postDto);
    }

    @DeleteMapping
    ResponseEntity<Void> deletePost(@RequestParam Long postId) {
        return communityPostService.deletePost(postId);
    }

    @PutMapping
    ResponseEntity<Void> updatePost(@RequestBody @Valid CommunityPostDto postDto) {
        return communityPostService.updatePost(postDto);
    }

    @PostMapping("/comment")
    ResponseEntity<PostCommentDto> addPostComment(@RequestBody @Valid PostCommentDto commentDto) {
        return communityPostService.addComment(commentDto);
    }

    @DeleteMapping("/comment")
    ResponseEntity<Void> deletePostComment(@RequestParam Long commentId) {
        return communityPostService.deleteComment(commentId);
    }
}

package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.CommunityPostDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.community.PostCommentDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICommunityPostService {
    ResponseEntity<Void> createPost(CommunityPostDto postDto);

    ResponseEntity<List<CommunityPostDto>> getPosts(CommunityEnums.EAccess access);

    ResponseEntity<Void> deletePost(Long postId);

    ResponseEntity<Void> updatePost(CommunityPostDto postDto);

    ResponseEntity<PostCommentDto> addComment(PostCommentDto commentDto);

    ResponseEntity<Void> deleteComment(Long commentId);
}

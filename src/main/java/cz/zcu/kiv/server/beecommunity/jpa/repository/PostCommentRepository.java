package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
}

package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import cz.zcu.kiv.server.beecommunity.jpa.entity.CommunityPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {
    List<CommunityPostEntity> findByAccessOrderById(CommunityEnums.EAccess access);

    @Query("SELECT r.author.email, COUNT(r) FROM CommunityPostEntity r where r.author.userInfo IS NOT NULL GROUP BY r.author ORDER BY COUNT(r) DESC LIMIT 1")
    Object findTopUserByPostCount();

    int countByAccess(CommunityEnums.EAccess access);

    @Query("SELECT COUNT(r) FROM CommunityPostEntity r GROUP BY r.created")
    List<Integer> countsDailyPosts();

    @Query("SELECT r.created, COUNT(r) FROM CommunityPostEntity r GROUP BY r.created ORDER BY r.created")
    List<Object[]> findCountPostsByCreatedDate();
}

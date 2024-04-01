package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.jpa.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    @Query("SELECT COUNT(r) FROM NewsEntity r GROUP BY r.date")
    List<Integer> countsDailyNews();

    @Query("SELECT r.author.email, COUNT(r) FROM NewsEntity r where r.author.userInfo IS NOT NULL GROUP BY r.author ORDER BY COUNT(r) DESC LIMIT 1")
    Object findTopUserByNewsCount();

    @Query("SELECT r.date, COUNT(r) FROM NewsEntity r GROUP BY r.date ORDER BY r.date")
    List<Object[]> findCountNewsByCreatedDate();
}

package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of news and tips for beekeepers
 */
@Entity
@Builder
@Table(name = "NEWS", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsEntity {

    /**
     * Unique identification of news entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "article")
    private String article;

    @Column(name = "title_image")
    private byte[] titleImage;

    @Column(name = "first_image")
    private byte[] firstImage;

    @Column(name = "second_image")
    private byte[] secondImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @Override
    public String toString() {
        return "NewsEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", article='" + article + '\'' +
                '}';
    }
}

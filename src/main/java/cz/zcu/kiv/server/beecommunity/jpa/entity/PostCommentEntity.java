package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "POST_COMMENT", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentEntity {
    /**
     * Unique identification of community entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private CommunityPostEntity post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity author;

    @Column(name = "comment")
    private String comment;

    @Column(name = "date")
    private LocalDate date;

    @Override
    public String toString() {
        return "PostCommentEntity{" +
                "id=" + id +
                ", author=" + author +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                '}';
    }
}

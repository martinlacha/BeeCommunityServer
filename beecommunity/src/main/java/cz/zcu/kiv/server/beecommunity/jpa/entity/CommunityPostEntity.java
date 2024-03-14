package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.CommunityEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;

import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "COMMUNITY_POST", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostEntity {
    /**
     * Unique identification of community entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity author;

    @Column(name = "title")
    private String title;

    @Column(name = "post")
    private String post;

    @Column(name = "image", length = 1024)
    private byte[] image;

    @Enumerated(EnumType.STRING)
    @Column(name = "access")
    private CommunityEnums.EAccess access;

    @Column(name = "created")
    private LocalDate created;

    @Column(name = "type")
    private CommunityEnums.EType type;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<PostCommentEntity> comments;

    @Override
    public String toString() {
        return "CommunityPostEntity{" +
                "id=" + id +
                ", author=" + author +
                ", post='" + post + '\'' +
                ", access=" + access +
                ", created=" + created +
                '}';
    }
}

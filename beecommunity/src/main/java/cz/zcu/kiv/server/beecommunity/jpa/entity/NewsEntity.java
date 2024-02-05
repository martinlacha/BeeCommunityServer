package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
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

    @Lob
    @Column(name = "title_image")
    private byte[] titleImage;

    @Lob
    @Column(name = "first_image")
    private byte[] firstImage;

    @Lob
    @Column(name = "second_image")
    private byte[] secondImage;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private UserEntity author;
}

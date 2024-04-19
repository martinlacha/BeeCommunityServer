package cz.zcu.kiv.server.beecommunity.jpa.entity;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of friendship between users
 */
@Entity
@Table(name = "FRIENDSHIP", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipEntity {

    /**
     * Unique identification of friendship entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FriendshipEnums.EStatus status;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserEntity receiver;
}

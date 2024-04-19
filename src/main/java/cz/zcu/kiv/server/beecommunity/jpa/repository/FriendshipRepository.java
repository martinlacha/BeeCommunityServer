package cz.zcu.kiv.server.beecommunity.jpa.repository;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    List<FriendshipEntity> findBySenderIdAndStatusOrReceiverIdAndStatus(
            Long senderId,
            FriendshipEnums.EStatus status1,
            Long receiverId,
            FriendshipEnums.EStatus status2
    );

    List<FriendshipEntity> findBySenderIdAndStatus(
            Long senderId,
            FriendshipEnums.EStatus status
    );

    List<FriendshipEntity> findByReceiverIdAndStatus(
            Long receiverId,
            FriendshipEnums.EStatus status
    );

    boolean existsBySenderIdAndStatusOrReceiverIdAndStatus(
            Long senderId,
            FriendshipEnums.EStatus status1,
            Long receiverId,
            FriendshipEnums.EStatus status2);

    boolean existsBySenderIdAndReceiverId(Long sender, Long receiver);

    Optional<FriendshipEntity> findBySenderIdAndReceiverId(Long sender, Long receiver);
}

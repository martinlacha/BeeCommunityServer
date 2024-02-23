package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.entity.FriendshipEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FriendshipUtils {
    private final FriendshipRepository friendshipRepository;

    /**
     * Find friendship has status between two users
     * @param myAccount account which send request
     * @param anotherAccount receiver account
     * @param status that friendship has to be
     * @return friendship entity if exists
     */
    public boolean isFriendshipStatus(Long myAccount, Long anotherAccount, FriendshipEnums.EStatus status) {
        Optional<FriendshipEntity> friendship =
                friendshipRepository.findBySenderIdAndReceiverId(myAccount, anotherAccount);
        if (friendship.isEmpty()) {
            friendship =
                    friendshipRepository.findBySenderIdAndReceiverId(anotherAccount, myAccount);
        }
        return friendship.isPresent() && friendship.get().getStatus().equals(status);
    }
}

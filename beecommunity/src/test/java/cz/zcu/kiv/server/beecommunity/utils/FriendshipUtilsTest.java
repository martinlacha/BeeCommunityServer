package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.entity.FriendshipEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.FriendshipRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FriendshipUtilsTest {
    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendshipUtils friendshipUtils;

    @Test
    void testIsFriendshipStatus() {
        Long myAccountId = 1L;
        Long anotherAccountId = 2L;
        FriendshipEnums.EStatus status = FriendshipEnums.EStatus.FRIEND;

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setStatus(status);

        // Mocking repository behavior
        when(friendshipRepository.findBySenderIdAndReceiverId(myAccountId, anotherAccountId))
                .thenReturn(Optional.of(friendshipEntity));

        boolean result = friendshipUtils.isFriendshipStatus(myAccountId, anotherAccountId, status);
        assertTrue(result);
    }

    @Test
    void testIsNotFriendshipStatus() {
        Long myAccountId = 1L;
        Long anotherAccountId = 2L;
        FriendshipEnums.EStatus status = FriendshipEnums.EStatus.FRIEND;
        // Mocking repository behavior (no friendship found)
        when(friendshipRepository.findBySenderIdAndReceiverId(myAccountId, anotherAccountId))
                .thenReturn(Optional.empty());
        boolean result = friendshipUtils.isFriendshipStatus(myAccountId, anotherAccountId, status);
        assertFalse(result);
    }
}

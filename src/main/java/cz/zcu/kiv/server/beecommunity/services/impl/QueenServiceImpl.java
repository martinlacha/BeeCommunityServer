package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.repository.HiveRepository;
import cz.zcu.kiv.server.beecommunity.services.IQueenService;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QueenServiceImpl implements IQueenService {
    private final HiveRepository hiveRepository;

    private final FriendshipUtils friendshipUtils;

    /**
     * Get queen image of specific hive if was uploaded
     * @param hiveId queen id
     * @return byte array of image
     */
    @Override
    public ResponseEntity<byte[]> getQueenImage(Long hiveId) {
        var user = UserUtils.getUserFromSecurityContext();
        var hive = hiveRepository.findById(hiveId);
        if (hive.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(hive.get().getOwner().getId()) &&
                !friendshipUtils.isFriendshipStatus(user.getId(), hive.get().getOwner().getId(), FriendshipEnums.EStatus.FRIEND)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (hive.get().getQueen().getImage() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ImageUtil.decompressImage(hive.get().getQueen().getImage()));
    }

}
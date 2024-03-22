package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import cz.zcu.kiv.server.beecommunity.jpa.repository.ApiaryRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.HiveRepository;
import cz.zcu.kiv.server.beecommunity.services.IHiveService;
import cz.zcu.kiv.server.beecommunity.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HiveServiceImpl implements IHiveService {
    private final ApiaryRepository apiaryRepository;

    private final HiveRepository hiveRepository;

    private final ObjectMapper modelMapper;

    private final FriendshipUtils friendshipUtils;

    /**
     * Find all hives a return them to user
     * @return list of hives
     */
    @Override
    public ResponseEntity<List<HiveDto>> getHives(Long apiaryId) {
        var user = UserUtils.getUserFromSecurityContext();
        var apiary = apiaryRepository.findById(apiaryId);
        if (apiary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!apiary.get().getOwner().getId().equals(user.getId()) &&
                !friendshipUtils.isFriendshipStatus(user.getId(), apiary.get().getOwner().getId(),
                        FriendshipEnums.EStatus.FRIEND)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var entitiesList = hiveRepository.findByApiaryIdOrderById(apiaryId);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertHiveEntityList(entitiesList));
    }

    /**
     * Create new hive
     * @param hiveDto hive dto with information
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> createHive(HiveDto hiveDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var apiary = apiaryRepository.findById(hiveDto.getApiaryId());
        if (apiary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!apiary.get().getOwner().getId().equals(user.getId()) &&
                !friendshipUtils.isFriendshipStatus(user.getId(), apiary.get().getOwner().getId(),
                        FriendshipEnums.EStatus.FRIEND)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var hiveEntity = modelMapper.convertHiveDto(hiveDto);
        hiveEntity.setOwner(user);
        hiveEntity.getQueen().setOwner(user);
        hiveRepository.saveAndFlush(hiveEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Update hive base information and queen
     * @param hiveDto dto with hive base information
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> updateHive(HiveDto hiveDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var hive = hiveRepository.findById(hiveDto.getId());
        if (hive.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(hive.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (!hiveDto.getApiaryId().equals(hive.get().getApiary().getId()) ||
            !hiveDto.getId().equals(hive.get().getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (hiveDto.getName() != null && !hiveDto.getName().isBlank()) {
            hive.get().setName(hiveDto.getName());
        }
        if (hiveDto.getColor() != null) {
            hive.get().setColor(hiveDto.getColor());
        }
        if (hiveDto.getSource() != null) {
            hive.get().setSource(hiveDto.getSource());
        }
        if (hiveDto.getEstablishment() != null) {
            hive.get().setEstablishment(DateTimeUtils.getDateFromString(hiveDto.getEstablishment()));
        }
        if (hiveDto.getNotes() != null && !hiveDto.getNotes().isBlank()) {
            hive.get().setNotes(hiveDto.getNotes());
        }

        if (hiveDto.getQueenName() != null && !hiveDto.getQueenName().isBlank()) {
            hive.get().getQueen().setName(hiveDto.getQueenName());
        }
        if (hiveDto.getBreed() != null && !hiveDto.getBreed().isBlank()) {
            hive.get().getQueen().setBreed(hiveDto.getBreed());
        }
        if (hiveDto.getHatch() != null && !hiveDto.getHatch().isBlank()) {
            hive.get().getQueen().setQueenHatch(DateTimeUtils.getDateFromString(hiveDto.getHatch()));
        }
        if (hiveDto.getQueenColor() != null) {
            hive.get().getQueen().setColor(hiveDto.getQueenColor());
        }
        if (hiveDto.getQueenNotes() != null && !hiveDto.getQueenNotes().isBlank()) {
            hive.get().getQueen().setNotes(hiveDto.getQueenNotes());
        }

        try {
            if (hiveDto.getImage() != null) {
                hive.get().setImage(ImageUtil.compressImage(hiveDto.getImage().getBytes()));
            }
            if (hiveDto.getQueenImage() != null) {
                hive.get().getQueen().setImage(ImageUtil.compressImage(hiveDto.getQueenImage().getBytes()));
            }
        } catch (IOException e) {
            log.warn("Error while update image for hive: {}", e.getMessage());
        }
        hiveRepository.saveAndFlush(hive.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Delete hive by id
     * @param hiveId id of hive to delete
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> deleteHive(Long hiveId) {
        var user = UserUtils.getUserFromSecurityContext();
        var hive = hiveRepository.findById(hiveId);
        if (hive.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(hive.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        hiveRepository.deleteById(hiveId);
        hiveRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Return detail structure of hive (supers, frames, queen)
     * @param hiveId hive id
     * @return hive detail
     */
    @Override
    public ResponseEntity<HiveDto> getHiveDetail(Long hiveId) {
        var user = UserUtils.getUserFromSecurityContext();
        var hive = hiveRepository.findById(hiveId);
        if (hive.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(hive.get().getOwner().getId()) &&
                !friendshipUtils.isFriendshipStatus(user.getId(), hive.get().getOwner().getId(), FriendshipEnums.EStatus.FRIEND)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertHiveEntity(hive.get()));
    }

    /**
     * Get image of hive if it was uploaded
     * @param hiveId hive id
     * @return byte array of hive image
     */
    @Override
    public ResponseEntity<byte[]> getHiveImage(Long hiveId) {
        var user = UserUtils.getUserFromSecurityContext();
        var hive = hiveRepository.findById(hiveId);
        if (hive.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(hive.get().getOwner().getId())  &&
                !friendshipUtils.isFriendshipStatus(user.getId(), hive.get().getOwner().getId(), FriendshipEnums.EStatus.FRIEND)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (hive.get().getImage() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ImageUtil.decompressImage(hive.get().getImage()));
    }

    /**
     * Find and return string representation of hive structure
     * @param hiveId hive id
     * @return String representation of structure
     */
    @Override
    public ResponseEntity<String> getHiveStructure(Long hiveId) {
        var user = UserUtils.getUserFromSecurityContext();
        var hive = hiveRepository.findById(hiveId);
        if (hive.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(hive.get().getOwner().getId()) &&
                !friendshipUtils.isFriendshipStatus(user.getId(), hive.get().getOwner().getId(), FriendshipEnums.EStatus.FRIEND)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(hive.get().getStructure());
    }

    /**
     * Create or update hive structure
     * @param hiveId hive id
     * @param structure string representation of hive structure
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> createHiveStructure(Long hiveId, String structure) {
        var user = UserUtils.getUserFromSecurityContext();
        var hive = hiveRepository.findById(hiveId);
        if (hive.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(hive.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        hive.get().setStructure(structure);
        hiveRepository.saveAndFlush(hive.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
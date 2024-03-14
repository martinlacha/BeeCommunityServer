package cz.zcu.kiv.server.beecommunity.services.impl;

import ch.qos.logback.core.util.TimeUtil;
import cz.zcu.kiv.server.beecommunity.jpa.dto.apiary.ApiaryDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import cz.zcu.kiv.server.beecommunity.jpa.repository.ApiaryRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.HiveRepository;
import cz.zcu.kiv.server.beecommunity.services.IApiaryService;
import cz.zcu.kiv.server.beecommunity.services.IHiveService;
import cz.zcu.kiv.server.beecommunity.utils.DateTimeUtils;
import cz.zcu.kiv.server.beecommunity.utils.ImageUtil;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HiveServiceImpl implements IHiveService {
    private final HiveRepository hiveRepository;

    private final ObjectMapper modelMapper;

    /**
     * Find all hives a return them to user
     * @return list of hives
     */
    @Override
    public ResponseEntity<List<HiveDto>> getHives(Long apiaryId) {
        var user = UserUtils.getUserFromSecurityContext();
        var entitiesList = hiveRepository.findByOwnerIdAndApiaryId(user.getId(), apiaryId);
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
        var hiveEntity = modelMapper.convertHiveDto(hiveDto);
        hiveEntity.setOwner(user);
        hiveRepository.saveAndFlush(hiveEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Update hive base information
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
        } else if (!user.getId().equals(hive.get().getOwner().getId())) {
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
        } else if (!user.getId().equals(hive.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (hive.get().getImage() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ImageUtil.decompressImage(hive.get().getImage()));
    }
}

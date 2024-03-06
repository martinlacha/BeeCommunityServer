package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.jpa.dto.apiary.ApiaryDto;
import cz.zcu.kiv.server.beecommunity.jpa.repository.ApiaryRepository;
import cz.zcu.kiv.server.beecommunity.services.IApiaryService;
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
public class ApiaryServiceImpl implements IApiaryService {
    private final ApiaryRepository apiaryRepository;

    private final ObjectMapper modelMapper;

    /**
     * Create new apiary for user
     * @param apiaryDto dto with info
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> createApiary(ApiaryDto apiaryDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var apiaryEntity = modelMapper.convertApiaryDto(apiaryDto);
        apiaryEntity.setOwner(user);
        apiaryRepository.saveAndFlush(apiaryEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Find all apiaries for user
     * @return list of apiaries
     */
    @Override
    public ResponseEntity<List<ApiaryDto>> getApiaries() {
        var user = UserUtils.getUserFromSecurityContext();
        var entitiesList = apiaryRepository.findByOwnerId(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertApiaryEntityList(entitiesList));
    }

    /**
     * Find image of apiary and return it
     * @param apiaryId apiary id
     * @return byte array of apiary image
     */
    @Override
    public ResponseEntity<byte[]> getApiaryImage(Long apiaryId) {
        var user = UserUtils.getUserFromSecurityContext();
        var apiary = apiaryRepository.findById(apiaryId);
        if (apiary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(apiary.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (apiary.get().getImage() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ImageUtil.decompressImage(apiary.get().getImage()));
    }

    /**
     * Delete apiary by id
     * @param apiaryId apiary id
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> deleteApiary(Long apiaryId) {
        var user = UserUtils.getUserFromSecurityContext();
        var apiary = apiaryRepository.findById(apiaryId);
        if (apiary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(apiary.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        apiaryRepository.deleteById(apiaryId);
        apiaryRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Update apiary information or image
     * @param apiaryDto dto with updated information
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> updateApiary(ApiaryDto apiaryDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var apiary = apiaryRepository.findById(apiaryDto.getId());
        if (apiary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(apiary.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (apiaryDto.getName() != null) {
            apiary.get().setName(apiaryDto.getName());
        }
        if (apiaryDto.getEnvironment() != null) {
            apiary.get().setEnvironment(apiary.get().getEnvironment());
        }
        if (apiaryDto.getTerrain() != null) {
            apiary.get().setTerrain(apiary.get().getTerrain());
        }
        if (Double.parseDouble(apiaryDto.getLatitude()) != 0) {
            apiary.get().setLatitude(apiary.get().getLatitude());
        }
        if (Double.parseDouble(apiaryDto.getLongitude()) != 0) {
            apiary.get().setLongitude(apiary.get().getLongitude());
        }
        if (apiaryDto.getNotes() != null) {
            apiary.get().setNotes(apiary.get().getNotes());
        }
        apiaryRepository.saveAndFlush(apiary.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Find apiary and return detail of it
     * @return dto with apiary details
     */
    @Override
    public ResponseEntity<ApiaryDto> getApiaryDetail(Long apiaryId) {
        var user = UserUtils.getUserFromSecurityContext();
        var apiary = apiaryRepository.findById(apiaryId);
        if (apiary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(apiary.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertApiaryEntity(apiary.get()));
    }
}

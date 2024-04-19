package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.apiary.ApiaryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IApiaryService {
    ResponseEntity<Void> createApiary(ApiaryDto postDto);

    ResponseEntity<List<ApiaryDto>> getApiaries();

    ResponseEntity<List<ApiaryDto>> getFriendApiaries(String email);

    ResponseEntity<byte[]> getApiaryImage(Long apiaryId);

    ResponseEntity<Void> deleteApiary(Long apiaryId);

    ResponseEntity<Void> updateApiary(ApiaryDto postDto);

    ResponseEntity<ApiaryDto> getApiaryDetail(Long apiaryId);
}

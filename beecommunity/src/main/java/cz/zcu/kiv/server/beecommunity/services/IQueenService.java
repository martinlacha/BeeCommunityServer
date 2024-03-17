package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IQueenService {
    ResponseEntity<byte[]> getQueenImage(Long hiveId);
}

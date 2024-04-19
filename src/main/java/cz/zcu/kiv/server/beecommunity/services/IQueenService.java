package cz.zcu.kiv.server.beecommunity.services;

import org.springframework.http.ResponseEntity;

public interface IQueenService {
    ResponseEntity<byte[]> getQueenImage(Long hiveId);
}

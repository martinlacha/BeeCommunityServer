package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.HiveDto;
import cz.zcu.kiv.server.beecommunity.jpa.dto.hive.SensorDataDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IHiveService {
    ResponseEntity<List<HiveDto>> getHives(Long apiaryId);

    ResponseEntity<Void> createHive(HiveDto hiveDto);

    ResponseEntity<Void> updateHive(HiveDto hiveDto);

    ResponseEntity<Void> deleteHive(Long hiveId);

    ResponseEntity<HiveDto> getHiveDetail(Long hiveId);

    ResponseEntity<byte[]> getHiveImage(Long hiveId);

    ResponseEntity<String> getHiveStructure(Long hiveId);

    ResponseEntity<Void> createHiveStructure(Long hiveId, String structure);

    ResponseEntity<Void> uploadSensorsData(Long hiveId, SensorDataDto data);

    ResponseEntity<List<SensorDataDto>> getHiveSensorsData(Long hiveId);
}

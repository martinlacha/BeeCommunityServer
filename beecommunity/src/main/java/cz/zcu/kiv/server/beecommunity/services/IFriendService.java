package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFriendService {
    ResponseEntity<List<FoundUserDto>> findUsers(String name);
}

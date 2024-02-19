package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.services.IFriendService;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
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
public class FriendServiceImpl implements IFriendService {

    private final UserRepository userRepository;

    private final ObjectMapper modelMapper;

    /**
     * Find all users that name, surname or email contain text ignoring case
     * Remove all users which don't have user info yet
     * @param name text that user has to contain to return
     * @return list of users with info about name, surname and email
     */
    @Override
    public ResponseEntity<List<FoundUserDto>> findUsers(String name) {
        List<UserEntity> list = userRepository
                .findByEmailContainsIgnoreCaseOrUserInfoNameContainsIgnoreCaseOrUserInfoSurnameContainsIgnoreCase(
                    name, name, name
                ).stream()
                .filter(userEntity -> userEntity.getUserInfo() != null).toList();
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.convertListUserEntity(list));
    }
}

package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.*;
import cz.zcu.kiv.server.beecommunity.jpa.entity.AddressEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.RoleEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserInfoEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.RoleRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.FriendshipUtils;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private FriendshipUtils friendshipUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private final TestData testData = new TestData();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        var user = UserEntity.builder().userInfo(null).build();
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String username = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        verify(userRepository).findByEmail(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistent@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository).findByEmail(username);
    }

    @Test
    void testCreateNewUser_UserNotExists() {
        NewUserDto newUserDto = new NewUserDto("test@example.com", "encodedPassword");
        RoleEntity userRole = new RoleEntity();
        userRole.setRole(UserEnums.ERoles.USER.name());
        when(roleRepository.findByRole(UserEnums.ERoles.USER.name())).thenReturn(Optional.of(userRole));

        when(objectMapper.convertToNewUserEntity(newUserDto)).thenReturn(UserEntity.builder().roles(new HashSet<>()).build());

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setEmail(newUserDto.getEmail());
        when(userRepository.existsByEmail(newUserDto.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(newUserDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.saveAndFlush(any())).thenReturn(newUserEntity);

        ResponseEntity<Void> responseEntity = userService.createNewUser(newUserDto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        verify(userRepository).existsByEmail(newUserDto.getEmail());
        verify(roleRepository).findByRole(UserEnums.ERoles.USER.name());
        verify(bCryptPasswordEncoder).encode(newUserDto.getPassword());
        verify(userRepository).saveAndFlush(any());
    }

    @Test
    void testCreateNewUser_UserExists() {
        NewUserDto newUserDto = new NewUserDto("test@example.com", "password");
        when(userRepository.existsByEmail(newUserDto.getEmail())).thenReturn(true);

        ResponseEntity<Void> responseEntity = userService.createNewUser(newUserDto);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        verify(userRepository).existsByEmail(newUserDto.getEmail());
        verifyNoMoreInteractions(roleRepository);
        verifyNoMoreInteractions(bCryptPasswordEncoder);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testCreateNewUserInfo_UserInfoNotExists() {
        var user = UserEntity.builder().userInfo(null).build();
        when(authentication.getPrincipal()).thenReturn(user);
        when(objectMapper.convertToUserInfoEntity(any())).thenReturn(UserInfoEntity.builder().dateOfBirth(LocalDate.now()).build());

        when(UserUtils.getUserFromSecurityContext()).thenReturn(user);

        ResponseEntity<Void> responseEntity = userService.createNewUserInfo(new NewUserInfoDto());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userRepository).saveAndFlush(user);
        assertEquals(LocalDate.now(), user.getUserInfo().getCreated());
    }

    @Test
    void testCreateNewUserInfo_UserInfoExists() {
        var user = UserEntity.builder().userInfo(new UserInfoEntity()).build();
        when(authentication.getPrincipal()).thenReturn(user);
        ResponseEntity<Void> responseEntity = userService.createNewUserInfo(new NewUserInfoDto());

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void testCreateNewUserInfo_MissingDateOfBirth() {
        when(objectMapper.convertToUserInfoEntity(any())).thenReturn(UserInfoEntity.builder().build());

        ResponseEntity<Void> responseEntity = userService.createNewUserInfo(new NewUserInfoDto());

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUserInfo_UserInfoExists() {
        var user = UserEntity
                .builder()
                .userInfo(UserInfoEntity.builder().address(new AddressEntity()).build())
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
        GetUpdateUserInfoDto userInfoDto = GetUpdateUserInfoDto
                .builder()
                .name("John")
                .surname("Dane")
                .dateOfBirth(LocalDate.now().toString())
                .experience(UserEnums.EExperience.NONE)
                .country("US")
                .state("NY")
                .town("New York")
                .street("Main st.")
                .number(125)
                .build();
        ResponseEntity<Void> responseEntity = userService.updateUserInfo(userInfoDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userRepository).saveAndFlush(any());
    }

    @Test
    void testUpdateUserInfo_UserInfoNotExists() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserInfo(null);

        ResponseEntity<Void> responseEntity = userService.updateUserInfo(new GetUpdateUserInfoDto());

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUserInfo_InvalidDateOfBirth() {
        var user = UserEntity.builder().userInfo(new UserInfoEntity()).build();
        when(authentication.getPrincipal()).thenReturn(user);
        GetUpdateUserInfoDto userInfoDto = new GetUpdateUserInfoDto();
        userInfoDto.setDateOfBirth("invalid_date");

        UserEntity userEntity = new UserEntity();
        userEntity.setUserInfo(new UserInfoEntity());

        ResponseEntity<Void> responseEntity = userService.updateUserInfo(userInfoDto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testResetUserPassword_UserFound() {
        String email = "user@example.com";
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        ResponseEntity<Void> responseEntity = userService.resetUserPassword(email);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetUserPassword_UserNotFound() {
        String email = "user@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<Void> responseEntity = userService.resetUserPassword(email);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verifyNoInteractions(emailSender);
    }

    @Test
    void testUpdatePassword_AccountNotFound() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        ResponseEntity<Void> responseEntity = userService.updatePassword(new UpdatePasswordDto());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testUpdatePassword_IncorrectCode() {
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto("test@mail.com", "new_password", "FS1fsA");
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        ResponseEntity<Void> responseEntity = userService.updatePassword(updatePasswordDto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(userRepository).findByEmail("test@mail.com");
        verifyNoMoreInteractions(userRepository, bCryptPasswordEncoder);
    }

    @Test
    void testUpdatePassword_Success() throws IllegalAccessException {
        String confirmCode = "ABCdef";
        HashMap<String, String> map = (HashMap<String, String>) FieldUtils.readField(userService, "RESET_PASSWORD_CODES_MAP", true);
        map.put("test@mail.com", confirmCode);

        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto("test@mail.com", confirmCode, "new_password");
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.encode(updatePasswordDto.getNewPassword())).thenReturn("encoded_password");

        ResponseEntity<Void> responseEntity = userService.updatePassword(updatePasswordDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userRepository).findByEmail("test@mail.com");
        verify(bCryptPasswordEncoder).encode(updatePasswordDto.getNewPassword());
        verify(userRepository).save(userEntity);
    }

    @Test
    void testGetUserInfo() {
        var user = UserEntity
                .builder()
                .userInfo(new UserInfoEntity())
                .roles(new HashSet<>())
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
        GetUpdateUserInfoDto userInfoDto = new GetUpdateUserInfoDto();
        userInfoDto.setName("John");
        userInfoDto.setSurname("Doe");
        userInfoDto.setDateOfBirth("1998-11-11");
        when(objectMapper.convertUserInfoDto(any())).thenReturn(userInfoDto);

        // Call the method
        ResponseEntity<GetUpdateUserInfoDto> response = userService.getUserInfo();

        // Check the response status
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check if the returned user info matches the mocked user info
        GetUpdateUserInfoDto returnedUserInfo = response.getBody();
        assertNotNull(returnedUserInfo);
        assertEquals("John", returnedUserInfo.getName());
        assertEquals("Doe", returnedUserInfo.getSurname());
        assertEquals("1998-11-11", returnedUserInfo.getDateOfBirth());
        // Check if the admin flag is correctly set
        assertFalse(returnedUserInfo.isAdmin());
    }

    @Test
    void testGetFriendUserInfo_NotFound() {
        String testEmail = "friendEmail@gmail.com";
        when(userRepository.findByEmail(eq(testEmail))).thenReturn(Optional.empty());
        // Call the method
        ResponseEntity<GetUpdateUserInfoDto> response = userService.getFriendUserInfo(testEmail);

        // Check the response status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify
        verify(userRepository,times(1)).findByEmail(testEmail);
        verifyNoInteractions(friendshipUtils, objectMapper);
    }

    @Test
    void testGetFriendUserInfo_NotFriend_BadRequest() {
        var friend = testData.getUser2();
        var user = UserEntity
                .builder()
                .userInfo(new UserInfoEntity())
                .roles(new HashSet<>())
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByEmail(eq(friend.getEmail()))).thenReturn(Optional.of(friend));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(friend.getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(false);

        // Call the method
        ResponseEntity<GetUpdateUserInfoDto> response = userService.getFriendUserInfo(friend.getEmail());

        // Check the response status
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verify
        verify(userRepository,times(1)).findByEmail(friend.getEmail());
        verify(friendshipUtils,times(1)).isFriendshipStatus(user.getId(), friend.getId(), FriendshipEnums.EStatus.FRIEND);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void testGetFriendUserInfo_Success() {
        var friend = testData.getUser2();
        var user = UserEntity
                .builder()
                .userInfo(new UserInfoEntity())
                .roles(new HashSet<>())
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByEmail(eq(friend.getEmail()))).thenReturn(Optional.of(friend));
        when(friendshipUtils.isFriendshipStatus(eq(user.getId()), eq(friend.getId()), eq(FriendshipEnums.EStatus.FRIEND))).thenReturn(true);

        // Call the method
        ResponseEntity<GetUpdateUserInfoDto> response = userService.getFriendUserInfo(friend.getEmail());

        // Check the response status
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUsersRoles() {
        var user = UserEntity
                .builder()
                .id(3L)
                .userInfo(new UserInfoEntity())
                .roles(new HashSet<>())
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
        List<UserEntity> users = List.of(
                UserEntity
                        .builder()
                        .id(1L)
                        .email("user0@example.com")
                        .roles(Set.of(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build()))
                        .build(),
                UserEntity
                        .builder()
                        .id(2L)
                        .roles(Set.of(RoleEntity.builder().role(UserEnums.ERoles.USER.name()).build()))
                        .email("user1@example.com")
                        .build()
        );

        // Mock user repository
        when(userRepository.findAll()).thenReturn(users);

        // Call the method
        ResponseEntity<List<UserRolesDto>> response = userService.getUsersRoles();

        // Check the response status
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check if the returned list of users with roles matches the expected values
        List<UserRolesDto> returnedUserRolesList = response.getBody();
        assertNotNull(returnedUserRolesList);
        assertEquals(2, returnedUserRolesList.size());

        UserRolesDto user1RolesDto = returnedUserRolesList.get(0);
        assertEquals(1L, user1RolesDto.getUserId());
        assertEquals("user0@example.com", user1RolesDto.getEmail());
        assertFalse(user1RolesDto.isUser());
        assertTrue(user1RolesDto.isAdmin());

        UserRolesDto user2RolesDto = returnedUserRolesList.get(1);
        assertEquals(2L, user2RolesDto.getUserId());
        assertEquals("user1@example.com", user2RolesDto.getEmail());
        assertTrue(user2RolesDto.isUser());
        assertFalse(user2RolesDto.isAdmin());
    }

    @Test
    void testGrantAdminRole_Success() {
        var user = UserEntity
                .builder()
                .id(1L)
                .userInfo(new UserInfoEntity())
                .roles(new HashSet<>())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(UserEnums.ERoles.ADMIN.name())).thenReturn(Optional.ofNullable(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build()));

        // Call the method
        ResponseEntity<Void> response = userService.grantAdminRole(1L);

        // Verify the method behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findByRole(UserEnums.ERoles.ADMIN.name());
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void testGrantAdminRole_UserNotFound() {
        // Mock data
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // Call the method
        ResponseEntity<Void> response = userService.grantAdminRole(userId);
        // Verify the method behavior
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGrantAdminRole_UserAlreadyAdmin() {
        var user = UserEntity
                .builder()
                .id(1L)
                .userInfo(new UserInfoEntity())
                .roles(Set.of(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build()))
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(UserEnums.ERoles.ADMIN.name())).thenReturn(Optional.ofNullable(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build()));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Call the method
        ResponseEntity<Void> response = userService.grantAdminRole(user.getId());

        // Verify the method behavior
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testRevokeAdminRole_Success() {
        var roles = new HashSet<RoleEntity>();
        roles.add(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build());
        var user = UserEntity
                .builder()
                .id(1L)
                .userInfo(new UserInfoEntity())
                .roles(roles)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(UserEnums.ERoles.ADMIN.name())).thenReturn(Optional.ofNullable(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build()));


        // Call the method
        ResponseEntity<Void> response = userService.revokeAdminRole(user.getId());

        // Verify the method behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(roleRepository, times(1)).findByRole(UserEnums.ERoles.ADMIN.name());
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void testRevokeAdminRole_UserNotFound() {
        // Mock data
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<Void> response = userService.revokeAdminRole(userId);

        // Verify the method behavior
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testRevokeAdminRole_UserNotAdmin() {
        var roles = new HashSet<RoleEntity>();
        roles.add(RoleEntity.builder().role(UserEnums.ERoles.USER.name()).build());
        var user = UserEntity
                .builder()
                .id(1L)
                .roles(roles)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authentication.getPrincipal()).thenReturn(user);
        when(roleRepository.findByRole(UserEnums.ERoles.ADMIN.name())).thenReturn(Optional.ofNullable(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build()));

        // Call the method
        ResponseEntity<Void> response = userService.revokeAdminRole(user.getId());

        // Verify the method behavior
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(roleRepository, times(1)).findByRole(UserEnums.ERoles.ADMIN.name());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testChangeUserEmail_Success() {
        var adminRoles = new HashSet<RoleEntity>();
        adminRoles.add(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build());
        var admin = UserEntity
                .builder()
                .id(3L)
                .roles(adminRoles)
                .build();
        when(authentication.getPrincipal()).thenReturn(admin);
        var roles = new HashSet<RoleEntity>();
        roles.add(RoleEntity.builder().role(UserEnums.ERoles.USER.name()).build());
        var user = UserEntity
                .builder()
                .id(1L)
                .userInfo(new UserInfoEntity())
                .roles(roles)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(UserEnums.ERoles.ADMIN.name())).thenReturn(Optional.ofNullable(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build()));


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newEmail@email.com")).thenReturn(false);

        // Call the method
        ResponseEntity<Void> response = userService.changeUserEmail(user.getId(), "newEmail@email.com");

        // Verify the method behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newEmail@email.com", user.getEmail());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).existsByEmail("newEmail@email.com");
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void testChangeUserEmail_UserNotFound() {
        // Mock data
        var adminRoles = new HashSet<RoleEntity>();
        adminRoles.add(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build());
        var admin = UserEntity
                .builder()
                .id(3L)
                .roles(adminRoles)
                .build();
        String newEmail = "newEmail@email.com";
        when(authentication.getPrincipal()).thenReturn(admin);
        var roles = new HashSet<RoleEntity>();
        roles.add(RoleEntity.builder().role(UserEnums.ERoles.USER.name()).build());
        var user = UserEntity
                .builder()
                .id(1L)
                .userInfo(new UserInfoEntity())
                .roles(roles)
                .build();

        // Call the method
        ResponseEntity<Void> response = userService.changeUserEmail(user.getId(), newEmail);

        // Verify the method behavior
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).existsByEmail("newEmail@email.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testChangeUserEmail_NewEmailExists() {
        // Mock data
        var adminRoles = new HashSet<RoleEntity>();
        adminRoles.add(RoleEntity.builder().role(UserEnums.ERoles.ADMIN.name()).build());
        var admin = UserEntity
                .builder()
                .id(3L)
                .roles(adminRoles)
                .build();
        String newEmail = "newEmail@email.com";
        when(authentication.getPrincipal()).thenReturn(admin);
        var roles = new HashSet<RoleEntity>();
        roles.add(RoleEntity.builder().role(UserEnums.ERoles.USER.name()).build());
        var user = UserEntity
                .builder()
                .id(1L)
                .userInfo(new UserInfoEntity())
                .roles(roles)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(newEmail)).thenReturn(true);

        // Call the method
        ResponseEntity<Void> response = userService.changeUserEmail(user.getId(), newEmail);

        // Verify the method behavior
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).existsByEmail(newEmail);
    }
}

package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import cz.zcu.kiv.server.beecommunity.jpa.dto.user.*;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserInfoEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.RoleRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.services.IUserService;
import cz.zcu.kiv.server.beecommunity.utils.ConfirmCodeGenerator;
import cz.zcu.kiv.server.beecommunity.utils.DateTimeUtils;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, IUserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ObjectMapper objectMapper;

    private final JavaMailSender emailSender;

    private final Map<String, String> RESET_PASSWORD_CODES_MAP = new HashMap<>();


    /**
     * Load user from database by username
     * @param username email address of user
     * @return UserEntity if exists in database otherwise exception is raised
     * @throws UsernameNotFoundException exception if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found by email"));
    }

    /**
     * Create new user and set basic information, role and store to database
     * @param user dto with email and password (already encoded from client)
     * @return status code. OK (200) user created, CONFLICT (409) user already exits with this email
     */
    @Override
    public ResponseEntity<Void> createNewUser(NewUserDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Can't create account with email {}. Already exists.", user.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        var userRole = roleRepository.findByRole(UserEnums.ERoles.USER.name());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserEntity newUser = objectMapper.convertToNewUserEntity(user);
        newUser.setNewAccount(true);
        newUser.setSuspended(false);
        userRole.ifPresent(role -> newUser.getRoles().add(role));
        userRepository.saveAndFlush(newUser);
        log.info("New account with email {} created.", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Create new record in database about user personal information and address
     * @param userIntoDto dto with user information
     * @return status code.
     * OK(200) info stored, conflict (409) when info already exists, bad request (400) some field is empty or missing
     */
    @Override
    public ResponseEntity<Void> createNewUserInfo(NewUserInfoDto userIntoDto) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        if (user.getUserInfo() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        UserInfoEntity userInfoEntity = objectMapper.convertToUserInfoEntity(userIntoDto);
        if (userInfoEntity.getDateOfBirth() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        userInfoEntity.setCreated(LocalDate.now());
        user.setUserInfo(userInfoEntity);
        user.setNewAccount(false);
        userRepository.saveAndFlush(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Update user information and address
     * @param userInfoDto dto object with user information
     * @return status code. OK (200), conflict (409) user info not exists
     */
    @Override
    public ResponseEntity<Void> updateUserInfo(GetUpdateUserInfoDto userInfoDto) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        if (user.getUserInfo() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        var info = user.getUserInfo();
        if (userInfoDto.getName() != null && !userInfoDto.getName().isBlank()) {
            info.setName(userInfoDto.getName());
        }
        if (userInfoDto.getSurname() != null && !userInfoDto.getSurname().isBlank()) {
            info.setSurname(userInfoDto.getSurname());
        }
        if (userInfoDto.getDateOfBirth() != null && !userInfoDto.getDateOfBirth().isBlank()) {
            LocalDate dob = DateTimeUtils.getDateFromString(userInfoDto.getDateOfBirth());
            if (dob == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            info.setDateOfBirth(dob);
        }
        if (userInfoDto.getExperience() != null) {
            info.setExperience(userInfoDto.getExperience());
        }

        var address = info.getAddress();
        if (userInfoDto.getCountry() != null && !userInfoDto.getCountry().isBlank()) {
            address.setCountry(userInfoDto.getCountry());
        }
        if (userInfoDto.getState() != null && !userInfoDto.getState().isBlank()) {
            address.setState(userInfoDto.getState());
        }
        if (userInfoDto.getTown() != null && !userInfoDto.getTown().isBlank()) {
            address.setTown(userInfoDto.getTown());
        }
        if (userInfoDto.getStreet() != null && !userInfoDto.getStreet().isBlank()) {
            address.setStreet(userInfoDto.getStreet());
        }
        if (userInfoDto.getNumber() > 0) {
            address.setNumber(userInfoDto.getNumber());
        }

        userRepository.saveAndFlush(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Send email with confirm code to change password
     * @param email dto with email where will be sent confirm code
     * @return status code. OK (200) email was send, account not found (404)
     */
    @Override
    public ResponseEntity<Void> resetUserPassword(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("Reset password failed for {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String code = ConfirmCodeGenerator.generateCode();
        RESET_PASSWORD_CODES_MAP.put(optionalUser.get().getEmail(), code);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("BeeCommunity: Reset password code");
        mailMessage.setText("Confirm code: " + code);
        try {
            emailSender.send(mailMessage);
            log.info("Reset password email sent to {}", email);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MailException e) {
            log.warn("Failed to send reset password email to {}", email, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update account password
     * Find account then check confirm code is valid and hash password and update account
     * @param updatePasswordDto dto with email, new password and confirm code from email
     * @return status code. Ok (200) password changed, bad request (400) confirm code is bad or account not found
     */
    @Override
    public ResponseEntity<Void> updatePassword(UpdatePasswordDto updatePasswordDto) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(updatePasswordDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.warn("Account {} not found", updatePasswordDto.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String code = RESET_PASSWORD_CODES_MAP.get(updatePasswordDto.getEmail());
        if (!updatePasswordDto.getCode().equals(code)) {
            log.warn("{} enter invalid confirm code for reset password.", updatePasswordDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        optionalUser.get().setPassword(bCryptPasswordEncoder.encode(updatePasswordDto.getNewPassword()));
        optionalUser.get().setLogin_attempts(0);
        RESET_PASSWORD_CODES_MAP.remove(updatePasswordDto.getEmail());
        userRepository.save(optionalUser.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Find and return user info if exits
     * @return OK (200) and user info if account found, otherwise NOT FOUND (404)
     */
    @Override
    public ResponseEntity<GetUpdateUserInfoDto> getUserInfo() {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        if (user.getUserInfo() == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var dto = objectMapper.convertUserInfoDto(user.getUserInfo());
        dto.setAdmin(user.hasRole(UserEnums.ERoles.ADMIN));
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /**
     * Return list of users with roles assigned to them
     * @return list of users with roles
     */
    @Override
    public ResponseEntity<List<UserRolesDto>> getUsersRoles() {
        var admin = UserUtils.getUserFromSecurityContext();
        var users = userRepository.findAll();
        var rolesList = new ArrayList<UserRolesDto>();
        users.forEach(user -> {
                    if (!admin.getId().equals(user.getId())) {
                        rolesList.add(
                                UserRolesDto
                                        .builder()
                                        .userId(user.getId())
                                        .email(user.getEmail())
                                        .fullName(user.getFullName())
                                        .isUser(user.hasRole(UserEnums.ERoles.USER))
                                        .isAdmin(user.hasRole(UserEnums.ERoles.ADMIN))
                                        .build()
                        );
                    }}
        );
        return ResponseEntity.status(HttpStatus.OK).body(rolesList);
    }

    /**
     * Grant user with admin role.
     * User has to be admin to grant other user.
     * @param userId id of user that will be granted admin role
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> grantAdminRole(Long userId) {
        var newAdmin = userRepository.findById(userId);
        var adminRole = roleRepository.findByRole(UserEnums.ERoles.ADMIN.name());
        if (newAdmin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (newAdmin.get().hasRole(UserEnums.ERoles.ADMIN) || adminRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        newAdmin.get().getRoles().add(adminRole.get());
        userRepository.saveAndFlush(newAdmin.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Revoke admin role from user.
     * @param userId user id
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> revokeAdminRole(Long userId) {
        var adminUser = userRepository.findById(userId);
        var adminRole = roleRepository.findByRole(UserEnums.ERoles.ADMIN.name());
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!adminUser.get().hasRole(UserEnums.ERoles.ADMIN) || adminRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        adminUser.get().getRoles().remove(adminRole.get());
        userRepository.saveAndFlush(adminUser.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Change user email
     * @param userId user id which will be changed email
     * @param newEmail new email of user
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> changeUserEmail(Long userId, String newEmail) {
        UserEntity user = UserUtils.getUserFromSecurityContext();
        var account = userRepository.findById(userId);
        if (!user.hasRole(UserEnums.ERoles.ADMIN) || userRepository.existsByEmail(newEmail)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        account.get().setEmail(newEmail);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

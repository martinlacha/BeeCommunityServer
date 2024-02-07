package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.config.PropertiesConfiguration;
import cz.zcu.kiv.server.beecommunity.jpa.dto.*;
import cz.zcu.kiv.server.beecommunity.jpa.entity.RoleEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.RoleRepository;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.services.IUserService;
import cz.zcu.kiv.server.beecommunity.utils.ConfirmCodeGenerator;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private final PropertiesConfiguration propertiesConfiguration;

    private final Map<String, String> RESET_PASSWORD_CODES_MAP = new HashMap<>();


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found by email"));
    }

    @Override
    public ResponseEntity<Void> createNewUser(NewUserDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Can't create account with email {}. Already exists.", user.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        List<RoleEntity> roles = roleRepository.findAll();
        RoleEntity userRole = roleRepository.findByRole("USER");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserEntity newUser = objectMapper.convertToNewUserEntity(user);
        newUser.setNewAccount(true);
        newUser.setSuspended(false);
        newUser.setRole(userRole);
        userRepository.saveAndFlush(newUser);
        log.info("New account with email {} created.", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> resetUserPassword(ResetPasswordDto resetPasswordDto) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(resetPasswordDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.warn("Reset password failed for {}", resetPasswordDto.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String code = ConfirmCodeGenerator.generateCode();
        RESET_PASSWORD_CODES_MAP.put(optionalUser.get().getEmail(), code);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreplay@seznam.cz");
        mailMessage.setTo(resetPasswordDto.getEmail());
        mailMessage.setSubject("BeeCommunity: Reset password code");
        mailMessage.setText("Code: " + code);
        emailSender.send(mailMessage);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<Void> createNewUserInfo(NewUserInfoDto userIntoDto) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object details = authentication.getDetails();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<Void> updateUserInfo(UpdateUserInfoDto userInfoDto) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<Void> updatePassword(UpdatePasswordDto updatePasswordDto) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(updatePasswordDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.warn("Account {} not found", updatePasswordDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String code = RESET_PASSWORD_CODES_MAP.get(updatePasswordDto.getEmail());
        if (!updatePasswordDto.getCode().equals(code)) {
            log.warn("{} enter invalid confirm code for reset password.", updatePasswordDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        optionalUser.get().setPassword(bCryptPasswordEncoder.encode(updatePasswordDto.getNewPassword()));
        optionalUser.get().setLogin_attempts(0);
        userRepository.save(optionalUser.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

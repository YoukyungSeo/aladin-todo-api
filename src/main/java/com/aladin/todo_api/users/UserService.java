package com.aladin.todo_api.users;

import com.aladin.todo_api.common.security.TokenProvider;
import com.aladin.todo_api.users.dto.LoginDto;
import com.aladin.todo_api.users.dto.ModifyDto;
import com.aladin.todo_api.users.dto.SignupDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = (Logger) LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final UserValidator userValidator;

    /*
    * SQLite3 단일 쓰기 대응
    */
    public Optional<User> getUserByUserId(String userId) { return userRepository.findById(userId); }

    /*
    * 아이디 존재 여부 검증
    */
    private User getUser(String userId) {
        User user = getUserByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 회원 정보를 찾을 수 없습니다."));
        return user;
    }

    /*
     * 비밀번호 확인
     */
    private void checkPassword(String inputPassword, String savedPassword) {
        if(!passwordEncoder.matches(inputPassword, savedPassword)) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }

    /*
    * 회원가입
    */
    @Transactional
    public User createUser(SignupDto dto) {
        // 입력값 검증
        userValidator.validateSignupDto(dto);

        User user = User.builder()
                .userId(dto.getUserId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .phoneNo(dto.getPhoneNo())
                .email(dto.getEmail())
                .build();

        return userRepository.save(user);
    }

    /*
    * 로그인(토큰 생성)
    */
    @Transactional
        public String loginUser(LoginDto dto) {
        
        User user = getUser(dto.getUserId());
        log.debug("사용자 ID : {}", dto.getUserId());

        // 비밀번호 확인
        checkPassword(dto.getPassword(), user.getPassword());

        return tokenProvider.generateToken(user.getUserId());
    }

    /*
    * 사용자 정보 조회
    */
    @Transactional
    public User getUser(Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        return getUser(userId);
    }

    /*
    * 사용자 정보 변경
    */
    @Transactional
    public User modifyUser(ModifyDto dto, Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        User user = getUser(userId);

        // 비밀번호 확인
        checkPassword(dto.getPassword(), user.getPassword());
        // 입력 값 검증
        userValidator.validateModifyDto(dto);

        // 비밀번호 변경
        Optional.ofNullable(dto.getNewPassword())
                .filter(StringUtils::hasText)
                .ifPresent(newPwd -> {
                    user.setPassword(passwordEncoder.encode(newPwd));
                });

        // 사용자 이름 변경
        Optional.ofNullable(dto.getUsername())
                .filter(StringUtils::hasText)
                .ifPresent(newName -> {
                    user.setUsername(newName);
                });

        // 전화번호 변경
        Optional.ofNullable(dto.getPhoneNo())
                .filter(StringUtils::hasText)
                .ifPresent(newPhone -> {
                    user.setPhoneNo(newPhone);
                });

        // 이메일 변경
        Optional.ofNullable(dto.getEmail())
                .filter(StringUtils::hasText)
                .ifPresent(newEmail -> {
                    user.setEmail(newEmail);
                });

        return userRepository.save(user);
    }

    @Transactional
    public User deleteUser(String password, Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        User user = getUser(userId);

        // 사용자 탈퇴 전 비밀번호 확인
        checkPassword(password, user.getPassword());
        userRepository.deleteById(userId);

        return user;
    }

}

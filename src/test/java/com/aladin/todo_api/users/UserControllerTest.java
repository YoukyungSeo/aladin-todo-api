package com.aladin.todo_api.users;

import com.aladin.todo_api.common.security.TokenProvider;
import com.aladin.todo_api.users.dto.LoginDto;
import com.aladin.todo_api.users.dto.ModifyDto;
import com.aladin.todo_api.users.dto.PasswordDto;
import com.aladin.todo_api.users.dto.SignupDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenProvider tokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String USER_ID = "aladinUser";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        createUser(USER_ID, "Password1!", "알라딘", "01012345678", "aladin@todo.com");
    }

    private void createUser(String userId, String password, String username, String phoneNo, String email) {
        User user = User.builder()
                .userId(userId)
                .password(passwordEncoder.encode(password))
                .username(username)
                .phoneNo(phoneNo)
                .email(email)
                .build();
        userRepository.save(user);
    }

    private String generateToken(String userId) {
        return tokenProvider.generateToken(userId);
    }

    @Test
    void 회원가입_성공() throws Exception {
        // Given: 회원가입 정보 준비
        SignupDto dto = SignupDto.builder()
                .userId("genieUser")
                .password("Password1!")
                .confirmPassword("Password1!")
                .username("지니")
                .phoneNo("01098765432")
                .email("genie@todo.com")
                .build();

        // When: 회원가입 API 요청
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Then: 성공 여부 및 응답 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."))
                .andExpect(jsonPath("$.data.userId").value("genieUser"));
    }

    @Test
    void 회원가입_실패_휴대폰번호_형식오류() throws Exception {
        // Given: 잘못된 휴대폰번호 형식
        SignupDto dto = SignupDto.builder()
                .userId("genieUser")
                .password("Password1!")
                .confirmPassword("Password1!")
                .username("지니")
                .phoneNo("010-1234-5678")
                .email("genie@todo.com")
                .build();

        // When: 회원가입 API 요청
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Then: 400 오류 검증
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("휴대폰 번호 형식이 올바르지 않습니다."));
    }

    @Test
    void JWT_없음_정보조회_실패_401() throws Exception {
        // Given: 토큰 없이 요청

        // When: 사용자 정보 조회 API 요청
        mockMvc.perform(get("/users/me"))
                // Then: 401 인증 오류 검증
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인_성공_JWT_발급() throws Exception {
        // Given: 로그인 정보 준비
        LoginDto dto = LoginDto.builder()
                .userId(USER_ID)
                .password("Password1!")
                .build();

        // When: 로그인 API 요청
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Then: JWT 발급 여부 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void 사용자_정보조회_성공() throws Exception {
        // Given: 올바른 accessToken
        String token = generateToken(USER_ID);

        // When: 사용자 정보 조회 API 요청
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                // Then: 조회 결과 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.userId").value(USER_ID));
    }

    @Test
    void 사용자_정보조회_실패_없는사용자() throws Exception {
        // Given: 존재하지 않는 사용자 토큰
        String token = generateToken("deletedUser");

        // When: 사용자 정보 조회 API 요청
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                // Then: 404 오류 검증
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("일치하는 회원 정보를 찾을 수 없습니다."));
    }

    @Test
    void 사용자_정보수정_성공() throws Exception {
        // Given: accessToken 및 수정 정보 준비
        String token = generateToken(USER_ID);

        ModifyDto dto = ModifyDto.builder()
                .password("Password1!")
                .newPassword("NewPassword1!")
                .username("자스민")
                .phoneNo("01099998888")
                .email("jasmine@todo.com")
                .build();

        // When: 사용자 정보 변경 API 요청
        mockMvc.perform(put("/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Then: 변경 결과 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보 변경에 성공했습니다."))
                .andExpect(jsonPath("$.data.username").value("자스민"));
    }

    @Test
    void 사용자_정보수정_실패_비밀번호불일치() throws Exception {
        // Given: accessToken 및 잘못된 비밀번호 입력
        String token = generateToken(USER_ID);

        ModifyDto dto = ModifyDto.builder()
                .password("WrongPassword1!")
                .newPassword("NewPassword1!")
                .username("자스민")
                .phoneNo("01099998888")
                .email("jasmine@todo.com")
                .build();

        // When: 사용자 정보 변경 API 요청
        mockMvc.perform(put("/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Then: 인증 실패 검증
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    void 회원탈퇴_성공() throws Exception {
        // Given: accessToken 및 비밀번호 정보
        String token = generateToken(USER_ID);

        PasswordDto dto = PasswordDto.builder()
                .password("Password1!")
                .build();

        // When: 회원탈퇴 API 요청
        mockMvc.perform(delete("/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Then: 탈퇴 결과 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 탈퇴에 성공했습니다."))
                .andExpect(jsonPath("$.data.userId").value(USER_ID));
    }
}

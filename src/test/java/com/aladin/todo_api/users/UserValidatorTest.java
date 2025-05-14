package com.aladin.todo_api.users;

import com.aladin.todo_api.users.dto.ModifyDto;
import com.aladin.todo_api.users.dto.SignupDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    void 아이디_형식_검증() {
        assertTrue(userValidator.isValidUserId("user01"));
        assertFalse(userValidator.isValidUserId("u@#"));
        assertFalse(userValidator.isValidUserId(null));
    }

    @Test
    void 비밀번호_형식_검증() {
        assertTrue(userValidator.isValidPassword("Password1!"));
        assertFalse(userValidator.isValidPassword("password"));
        assertFalse(userValidator.isValidPassword(null));
    }

    @Test
    void 휴대폰번호_형식_검증() {
        assertTrue(userValidator.isValidPhoneNo("01012345678"));
        assertFalse(userValidator.isValidPhoneNo("010-1234-5678"));
        assertFalse(userValidator.isValidPhoneNo("02012345678"));
        assertFalse(userValidator.isValidPhoneNo(null));
    }

    @Test
    void 이메일_형식_검증() {
        assertTrue(userValidator.isValidEmail("test@todo.com"));
        assertFalse(userValidator.isValidEmail("test@todo"));
        assertFalse(userValidator.isValidEmail(null));
    }

    @Test
    void 사용자이름_형식_검증() {
        assertTrue(userValidator.isValidUsername("홍길동"));
        assertFalse(userValidator.isValidUsername("John"));
        assertFalse(userValidator.isValidUsername(null));
    }

    @Test
    void SignupDto_유효성_검증_성공() {
        SignupDto dto = SignupDto.builder()
                .userId("user01")
                .password("Password1!")
                .confirmPassword("Password1!")
                .username("홍길동")
                .phoneNo("01012345678")
                .email("test@todo.com")
                .build();

        assertDoesNotThrow(() -> userValidator.validateSignupDto(dto));
    }

    @Test
    void SignupDto_유효성_검증_비밀번호불일치() {
        SignupDto dto = SignupDto.builder()
                .userId("user01")
                .password("Password1!")
                .confirmPassword("Different1!")
                .username("홍길동")
                .phoneNo("01012345678")
                .email("test@todo.com")
                .build();

        Exception e = assertThrows(IllegalArgumentException.class, () -> userValidator.validateSignupDto(dto));
        assertEquals("비밀번호가 일치하지 않습니다.", e.getMessage());
    }

    @Test
    void SignupDto_유효성_검증_잘못된_아이디() {
        SignupDto dto = SignupDto.builder()
                .userId("u@#")
                .password("Password1!")
                .confirmPassword("Password1!")
                .username("홍길동")
                .phoneNo("01012345678")
                .email("test@todo.com")
                .build();

        Exception e = assertThrows(IllegalArgumentException.class, () -> userValidator.validateSignupDto(dto));
        assertEquals("아이디 형식이 올바르지 않습니다.", e.getMessage());
    }

    @Test
    void ModifyDto_유효성_검증_성공() {
        ModifyDto dto = ModifyDto.builder()
                .newPassword("Password1!")
                .username("홍길동")
                .phoneNo("01012345678")
                .email("test@todo.com")
                .build();

        assertDoesNotThrow(() -> userValidator.validateModifyDto(dto));
    }

    @Test
    void ModifyDto_유효성_검증_잘못된_휴대폰번호() {
        ModifyDto dto = ModifyDto.builder()
                .phoneNo("010-1234-5678")
                .build();

        Exception e = assertThrows(IllegalArgumentException.class, () -> userValidator.validateModifyDto(dto));
        assertEquals("변경된 휴대폰 번호 형식이 올바르지 않습니다.", e.getMessage());
    }

    @Test
    void ModifyDto_유효성_검증_잘못된_이메일() {
        ModifyDto dto = ModifyDto.builder()
                .email("invalidEmail")
                .build();

        Exception e = assertThrows(IllegalArgumentException.class, () -> userValidator.validateModifyDto(dto));
        assertEquals("변경된 이메일 형식이 올바르지 않습니다.", e.getMessage());
    }

    @Test
    void ModifyDto_유효성_검증_잘못된_이름() {
        ModifyDto dto = ModifyDto.builder()
                .username("John")
                .build();

        Exception e = assertThrows(IllegalArgumentException.class, () -> userValidator.validateModifyDto(dto));
        assertEquals("변경된 사용자 이름 형식이 올바르지 않습니다.", e.getMessage());
    }
}

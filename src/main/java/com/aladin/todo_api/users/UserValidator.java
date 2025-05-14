package com.aladin.todo_api.users;

import com.aladin.todo_api.users.dto.ModifyDto;
import com.aladin.todo_api.users.dto.SignupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    // 아이디 정규식: 4~12자 영문자 또는 숫자 (특수문자 제외)
    private static final String ID_REGEX = "^[a-zA-Z0-9]{4,12}$";
    // 비밀번호 정규식: 영문, 숫자, 특수문자 중 2가지 이상 조합하여 10자~20자
    private static final String PWD_REGEX = "^(?=(.*[A-Za-z].*[0-9]|.*[A-Za-z].*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]|.*[0-9].*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]))[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{10,20}$";
    // 휴대폰 번호 정규식: 01*-****-**** (하이픈 제외)
    private static final String PHONENO_REGEX = "^01[0|1|6|7|8|9]\\d{3,4}\\d{4}$";
    // 이메일 정규식: email@domain.com
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    // 사용자 이름 정규식: 한글 2~5글자
    private static final String NAME_REGEX = "^[가-힣]{2,5}$";

    public boolean isValidUserId(String userId) {
        return userId != null && userId.matches(ID_REGEX);
    }
    public boolean isValidPassword(String password) {
        return password != null && password.matches(PWD_REGEX);
    }
    public boolean isValidPhoneNo(String phoneNo) {
        return phoneNo != null && phoneNo.matches(PHONENO_REGEX);
    }
    public boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
    public boolean isValidUsername(String username) {
        return username != null && username.matches(NAME_REGEX);
    }

    public void validateSignupDto(SignupDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (!this.isValidUserId(dto.getUserId())) {
            throw new IllegalArgumentException("아이디 형식이 올바르지 않습니다.");
        }
        if (!this.isValidPassword(dto.getPassword())) {
            throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다.");
        }
        if (!this.isValidPhoneNo(dto.getPhoneNo())) {
            throw new IllegalArgumentException("휴대폰 번호 형식이 올바르지 않습니다.");
        }
        if (!this.isValidEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        if (!this.isValidUsername(dto.getUsername())) {
            throw new IllegalArgumentException("사용자 이름 형식이 올바르지 않습니다.");
        }
    }

    public void validateModifyDto(ModifyDto dto) {
        if (dto.getNewPassword() != null && !this.isValidPassword(dto.getNewPassword())) {
            throw new IllegalArgumentException("변경된 비밀번호 형식이 올바르지 않습니다.");
        }
        if (dto.getPhoneNo() != null && !this.isValidPhoneNo(dto.getPhoneNo())) {
            throw new IllegalArgumentException("변경된 휴대폰 번호 형식이 올바르지 않습니다.");
        }
        if (dto.getEmail() != null && !this.isValidEmail(dto.getEmail())) {
            throw new IllegalArgumentException("변경된 이메일 형식이 올바르지 않습니다.");
        }
        if (dto.getUsername() != null && !this.isValidUsername(dto.getUsername())) {
            throw new IllegalArgumentException("변경된 사용자 이름 형식이 올바르지 않습니다.");
        }
    }

}

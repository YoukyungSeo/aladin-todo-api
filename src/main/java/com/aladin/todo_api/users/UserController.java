package com.aladin.todo_api.users;

import com.aladin.todo_api.common.response.ResponseUtil;
import com.aladin.todo_api.users.dto.LoginDto;
import com.aladin.todo_api.users.dto.ModifyDto;
import com.aladin.todo_api.users.dto.PasswordDto;
import com.aladin.todo_api.users.dto.SignupDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    /*
    * 회원가입
    * */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto dto) {
        if(userService.getUserByUserId(dto.getUserId()).isPresent()) {
          throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = userService.createUser(dto);
        return ResponseUtil.response("회원가입에 성공했습니다.", user);
    }

    /*
     * 로그인
     * */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        String accessToken = userService.loginUser(dto);
        return ResponseUtil.response("로그인에 성공했습니다.", accessToken);
    }

    /*
     * 내 정보 찾기(username)
     * */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        User user = userService.getUser(authentication);
        return ResponseUtil.response("사용자 정보 조회에 성공했습니다.", user);
    }

    /*
     * 내 정보 수정(username, password)
     * */
    @PutMapping("/me")
    public ResponseEntity<?> modifyMe(@RequestBody ModifyDto dto, Authentication authentication) {
        User user = userService.modifyUser(dto, authentication);
        return ResponseUtil.response("사용자 정보 변경에 성공했습니다.", user);
    }

    /*
     * 회원 탈퇴
     * */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMe(@RequestBody PasswordDto dto, Authentication authentication) {
        User user = userService.deleteUser(dto.getPassword(), authentication);
        return ResponseUtil.response("회원 탈퇴에 성공했습니다.", user);
    }

}

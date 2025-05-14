package com.aladin.todo_api.common;

import com.aladin.todo_api.common.security.TokenProvider;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {

    private TokenProvider tokenProvider;

    private static final String USER_ID = "aladinUser";
    private final String secretKeyString = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
    private final long tokenValidityInSeconds = 2;

    @BeforeEach
    void 초기화() {
        tokenProvider = new TokenProvider();
        tokenProvider.setSecretKeyString(secretKeyString);
        tokenProvider.setTokenValidityInSeconds(tokenValidityInSeconds);
        tokenProvider.init();
    }

    @Test
    void 토큰_정상_발급과_검증() {
        String token = tokenProvider.generateToken(USER_ID);

        assertNotNull(token, "토큰이 정상적으로 발급되지 않았습니다.");
        String validatedUserId = tokenProvider.validateToken(token);
        assertEquals(USER_ID, validatedUserId, "발급한 유저 ID와 검증 결과가 일치하지 않습니다.");
    }

    @Test
    void 만료된_토큰_검증시_예외_발생() throws InterruptedException {
        String token = tokenProvider.generateToken(USER_ID);

        Thread.sleep(3000);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(token));
        assertTrue(exception.getMessage().contains("만료된 JWT입니다."));
    }

    @Test
    void 변조된_토큰_검증시_예외_발생() {
        String token = tokenProvider.generateToken(USER_ID);
        String fakeToken = token.substring(0, token.length() - 2) + "ab";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(fakeToken));
        assertTrue(exception.getMessage().contains("잘못된 JWT 서명입니다."));
    }

    @Test
    void 빈값_토큰_검증시_예외_발생() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(""));
        assertTrue(exception.getMessage().contains("JWT 데이터가 비어있습니다."));
    }
}

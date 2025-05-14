package com.aladin.todo_api.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Setter
public class TokenProvider {

    private final Logger log = (Logger) LoggerFactory.getLogger(TokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds;

    private SecretKey secretKey;
    
    /*
    * 초기화
    * */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    * JWT 생성
    */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(userId) // sub claim: 사용자 ID
                .setIssuedAt(now) // iat: 발급 시간
                .setExpiration(expiryDate) // exp: 만료 시간
                .signWith(SignatureAlgorithm.HS512, secretKey) // 서명
                .compact();
    }

    /*
    * JWT 검증 및 파싱
    */
    public String validateToken(String token) {
        try{

            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return claimsJws.getBody().getSubject(); // 사용자 ID 반환

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new RuntimeException("만료된 JWT입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new RuntimeException("지원하지 않는 JWT 형식입니다.", e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new RuntimeException("손상된 JWT입니다.", e);
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new RuntimeException("잘못된 JWT 서명입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Empty JWT claims: {}", e.getMessage());
            throw new RuntimeException("JWT 데이터가 비어있습니다.", e);
        } catch (Exception e) {
            log.error("Unknown error while validating JWT: {}", e.getMessage());
            throw new RuntimeException("서버 내부 오류가 발생했습니다.", e);
        }
    }
}

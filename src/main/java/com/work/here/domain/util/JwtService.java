package com.work.here.domain.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.work.here.domain.entity.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}") // 환경변수 또는 설정파일에서 관리
    private String JWT_SECRET;

    @Value("${jwt.access}") // 액세스 토큰의 유효 기간 설정
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh}") // 리프레시 토큰의 유효 기간 설정
    private long REFRESH_TOKEN_EXPIRATION;

    // 사용자 ID 추출 메서드
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> Long.parseLong(claims.get("userId").toString()));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 예외 처리 (로그 기록 등)
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final Long userId = extractUserId(token);
        return (userId.equals(((User) userDetails).getId()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(Long userId, UserDetails userDetails) {

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("id", userId) // ID를 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION)) // 설정한 유효 기간 적용
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    public String generateRefreshToken( Long userId, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // 사용자 ID를 claims에 추가
        return createToken(claims, userDetails.getUsername(), REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 설정한 유효 기간 적용
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
}

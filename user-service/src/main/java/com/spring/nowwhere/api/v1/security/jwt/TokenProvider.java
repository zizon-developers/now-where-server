package com.spring.nowwhere.api.v1.security.jwt;

import com.spring.nowwhere.api.v1.entity.user.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

@Slf4j
public class TokenProvider {
    private Environment env;
    public TokenProvider(Environment env) {
        this.env = env;
    }

    public String generateJwtAccessToken(User user) {
        return setJwt(user, generateAccessTokenExpireTime(), env.getProperty("token.access-secret"));
    }

    public String generateJwtRefreshToken(User user) {
        return setJwt(user, createExpireDateForOneYear(), env.getProperty("token.refresh-secret"));
    }

    private String setJwt(User user, Date expireDate, String secret) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getName())
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, createSigningKey(secret));
        return builder.compact();
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());

        return header;
    }

    private Map<String, Object> createClaims(User user) {
        // 공개 클레임에 사용자의 이름과 이메일을 설정하여 정보를 조회할 수 있다.
        Map<String, Object> claims = new HashMap<>();

        claims.put("email", user.getEmail());
        claims.put("checkId", user.getCheckId());
        claims.put("role", user.getRoles());

        return claims;
    }

    private boolean isValidToken(String token,String secret) {
        try {
            Claims claims = getClaimsFormToken(token, secret);
            log.info("expireTime:{}",claims.getExpiration());
            log.info("email:{}",claims.get("email"));
            log.info("role:{}",claims.get("role"));
            return true;

        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");
            return false;
        } catch (JwtException exception) {
            log.error("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            log.error("Token is null");
            return false;
        }
    }

    public String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

    private Date generateAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() +
                Long.parseLong(env.getProperty("token.expiration-time")));
    }

    private static Date createExpireDateForOneYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 14);
        return c.getTime();
    }

    private Key createSigningKey(String secret) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    //parser() ->  throws ExpiredJwtException, MalformedJwtException, SignatureException
    private Claims getClaimsFormToken(String token, String secret)  {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parseClaimsJws(token).getBody();
    }

    public String getUserEmailFromAccessToken(String token) {
        Claims claims = getClaimsFormToken(token, env.getProperty("token.access-secret"));
        return (String) claims.get("email");
    }
    public String getCheckIdFromAccessToken(String token) {
        Claims claims = getClaimsFormToken(token, env.getProperty("token.access-secret"));
        return (String) claims.get("checkId");
    }
    public String getUserEmailFromRefreshToken(String token) {
        Claims claims = getClaimsFormToken(token, env.getProperty("token.refresh-secret"));
        return (String) claims.get("email");
    }

    public Date getExpireTimeFromAccessToken(String token){
        Claims claims = getClaimsFormToken(token, env.getProperty("token.access-secret"));
        return claims.getExpiration();
    }
    public Date getExpireTimeFromRefreshToken(String token){
        Claims claims = getClaimsFormToken(token, env.getProperty("token.refresh-secret"));
        return claims.getExpiration();
    }
}

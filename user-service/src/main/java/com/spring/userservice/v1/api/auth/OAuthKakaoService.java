package com.spring.userservice.v1.api.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spring.userservice.v1.api.auth.exception.DuplicateUserException;
import com.spring.userservice.v1.api.auth.exception.OauthKakaoApiException;
import com.spring.userservice.v1.api.entity.UserRepository;
import com.spring.userservice.v1.api.redis.logout.LogoutAccessTokenFromRedis;
import com.spring.userservice.v1.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthKakaoService {
    private final RestTemplate restTemplate;
    private final Environment evn;

    public TokenDto getKakaoToken(String code) {
        ResponseEntity<Map> response = null;
        String accessToken = "";
        String refreshToken = "";
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.token-uri");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
            body.add("grant_type", evn.getProperty("spring.security.oauth2.client.registration.kakao.authorization-grant-type"));
            body.add("client_id", evn.getProperty("spring.security.oauth2.client.registration.kakao.client-id"));
            body.add("client_secret", evn.getProperty("spring.security.oauth2.client.registration.kakao.client-secret"));
            body.add("redirect_uri", evn.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri"));
            body.add("code", code);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);


            response = restTemplate.exchange(reqURL, HttpMethod.POST, entity, Map.class);
            log.info("status code ={}",response.getStatusCode());
            log.info("bode ={}",response.getBody());

            if (response.getStatusCode() != HttpStatus.OK)
                throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());

            Map responseBody = response.getBody();
            accessToken = (String) responseBody.get("access_token");
            refreshToken = (String) responseBody.get("refresh_token");
            log.info("access ={}",accessToken);
            log.info("refresh ={}",refreshToken);

            return new TokenDto(accessToken, refreshToken);

        } catch (Exception e) {
            log.error("error",e);
            /**
             * 400 Bad Request: "{"error":"invalid_grant","error_description":"authorization code not found for code=ogATdbLnCaE8xIiVDWl5A3x9ymgEXzlSYEq406SuTFRNCTqiuscWSFZngTXnxXCoIc07ygo9dRoAAAGKFt8jag","error_code":"KOE320"}"
             * 예외처리하기
             */
            throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
        }
    }

    public OAuthUserDto getKakaoUser(String accessToken) {

        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.user-info-uri");
        ResponseEntity<String> response = null;
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            response = restTemplate.exchange(reqURL, HttpMethod.POST, entity, String.class);

            log.info("status ={}",response.getStatusCode());
            if (response.getStatusCode() != HttpStatus.OK)
                throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());

            String responseBody = response.getBody();
            log.info("body ={}",responseBody);


            //Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(responseBody);

            String id = element.getAsJsonObject().get("id").getAsString();
            String name = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            //email_needs_agreement = true 면 이메일 동의 항목에 사용자 동의 필요하다 라는 뜻
            boolean hasAgreedToEmails = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email_needs_agreement").getAsBoolean();
            Optional<String> email = Optional.empty();
            if(!hasAgreedToEmails){
                email = Optional.of(element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString());
            }
            log.info("id ={}" ,id);
            log.info("name ={}",name);
            log.info("email ={}" ,email);

            OAuthUserDto user = OAuthUserDto.builder()
                            .userId(id)
                            .name(name)
                            .email(email.orElse(id))
                            .build();

            return user;

        } catch (DuplicateUserException ex){
            throw ex;
        } catch (Exception e) {
            log.error("error",e);
            throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
        }
    }

    public Map<String, Object> getKakaoFriends(String accessToken) {

        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.friend-uri");
        ResponseEntity<Map> response = null; //예외처리 할 때 catch에서 사용하려고

        try {
            log.info(accessToken);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);



            URI targetUrl = UriComponentsBuilder
                    .fromUriString(reqURL) // 기본 URL
                    .queryParam("limit", 15)
                    .build()
                    .encode(StandardCharsets.UTF_8) // 인코딩
                    .toUri();

            response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
            }
        } catch (Exception e){
            log.error("error",e);
            throw e;
        }


    }
}

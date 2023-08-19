package com.spring.userservice.v1.api.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spring.userservice.v1.api.auth.exception.KakaoFriendsException;
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
    private final UserService userService;

    public void getKakaoAccessToken (String code) {
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
                throw new KakaoFriendsException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());

            Map responseBody = response.getBody();
            accessToken = (String) responseBody.get("access_token");
            refreshToken = (String) responseBody.get("refresh_token");
            log.info("access ={}",accessToken);
            log.info("refresh ={}",refreshToken);


        } catch (Exception e) {
            log.error("error",e);
            throw new KakaoFriendsException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
        }
        createKakaoUser(accessToken);
    }

    private void createKakaoUser(String accessToken) {

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
                throw new KakaoFriendsException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());

            String responseBody = response.getBody();
            log.info("body ={}",responseBody);


            //Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(responseBody);

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            log.info("id ={}" ,id);
            log.info("email ={}" ,email);

        } catch (Exception e) {
            log.error("error",e);
            throw new KakaoFriendsException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
        }
    }

    public Map<String, Object> getKakaoFriends(String accessToken) {

        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.friend-uri");
        ResponseEntity<Map> response = null;

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
                throw new KakaoFriendsException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
            }
        } catch (Exception e){
            log.error("error",e);
            throw new KakaoFriendsException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
        }
    }
}

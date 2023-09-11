package com.spring.nowwhere.api.v1.auth;

import com.google.gson.*;
import com.spring.nowwhere.api.v1.auth.dto.KaKaoFriendDto;
import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.auth.dto.TokenDto;
import com.spring.nowwhere.api.v1.auth.exception.OauthKakaoApiException;
import com.spring.nowwhere.api.v1.entity.friend.service.FriendService;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
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

import static org.springframework.web.client.HttpClientErrorException.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthKakaoService {

    private final RestTemplate restTemplate;
    private final FriendService friendService;
    private final Environment evn;

    public TokenDto getKakaoToken(String code) {
        String accessToken = "";
        String refreshToken = "";
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.token-uri");

        try {
            ResponseEntity<Map> response = null;
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
            log.info("status code ={}", response.getStatusCode());
            log.info("bode ={}", response.getBody());

            if (response.getStatusCode() != HttpStatus.OK)
                throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());

            Map responseBody = response.getBody();
            accessToken = (String) responseBody.get("access_token");
            refreshToken = (String) responseBody.get("refresh_token");
            log.info("access ={}", accessToken);
            log.info("refresh ={}", refreshToken);

            return new TokenDto(accessToken, refreshToken);

        } catch (BadRequest | Unauthorized | Forbidden e) {
            throw e;
        } catch (Exception e) {
            log.error("error", e);
            throw e;
        }
    }

    public OAuthUserDto getKakaoUser(String accessToken) {

        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.user-info-uri");
        try {
            ResponseEntity<String> response = null;
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, JwtProperties.TOKEN_PREFIX + accessToken);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            response = restTemplate.exchange(reqURL, HttpMethod.POST, entity, String.class);

            log.info("status ={}", response.getStatusCode());
            if (response.getStatusCode() != HttpStatus.OK)
                throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());

            String responseBody = response.getBody();
            log.info("body ={}", responseBody);


            //Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(responseBody);

            String id = element.getAsJsonObject().get("id").getAsString();
            String name = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            String profileImg = element.getAsJsonObject().get("properties").getAsJsonObject().get("thumbnail_image").getAsString();

            //optional 하려고했는데 Json예외 발생함
            boolean hasAgreedToEmails = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email_needs_agreement").getAsBoolean();
            Optional<String> optEmail = Optional.empty();
            if (!hasAgreedToEmails) {
                optEmail = Optional.of(element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString());
            }

            log.info("id ={}", id);
            log.info("name ={}", name);
            log.info("email ={}", optEmail);

            String email = optEmail.orElse(id);
            OAuthUserDto user = OAuthUserDto.builder()
                    .checkId(id)
                    .name(name)
                    .email(email)
                    .profileImg(profileImg)
                    .build();

            return user;
        } catch (BadRequest | Unauthorized | Forbidden e) {
            throw e;
        } catch (Exception e) {
            log.error("error", e);
            throw e;
        }
    }

    public Map<String, Object> getKakaoFriends(String accessToken, KaKaoFriendDto kaKaoFriendDto) {

        try {
            String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.friend-uri");
            ResponseEntity<Map> response = null; //예외처리 할 때 catch에서 사용하려고

            log.info(accessToken);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI targetUrl = UriComponentsBuilder.fromUriString(reqURL)
                    .queryParam("offset", kaKaoFriendDto.getOffset())
                    .queryParam("limit", kaKaoFriendDto
                            .getLimit().filter(integer -> integer <= 100 && integer > 0)
                            .orElse(10))
                    .queryParam("order", kaKaoFriendDto.getOrder())
                    .queryParam("friend_order", kaKaoFriendDto.getFriendOrder())
                    .build().encode(StandardCharsets.UTF_8)
                    .toUri();

            response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
            }
        } catch (BadRequest | Unauthorized | Forbidden e) {
            throw e;
        } catch (Exception e) {
            log.error("error", e);
            throw e;
        }
    }
    public void inviteFriendRegistration(String invitedUserCheckId, String invitingUserCheckId) {
        friendService.saveFriendshipFromInvitation(invitingUserCheckId, invitedUserCheckId);
    }

    public ResponseEntity<Object> createKakaoPayPaymentURL(String userId, int amount,String accessToken) {

        String reqURL = String.format(evn.getProperty("spring.security.oauth2.client.provider.kakao.pay-uri"), userId, toHexValue(amount));
        ResponseEntity<Object> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, JwtProperties.TOKEN_PREFIX + accessToken);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            response = restTemplate.exchange(reqURL, HttpMethod.POST, entity, Object.class);

            log.info("status ={}", response.getStatusCode());
            if (response.getStatusCode() != HttpStatus.OK)
                throw new OauthKakaoApiException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());


        } catch (BadRequest | Unauthorized | Forbidden e) {
            throw e;
        } catch (Exception e) {
            log.error("error", e);
            throw e;
        }
        return response;
    }
    private String toHexValue(int value){
        return Integer.toHexString((value * 524288));
    }


}
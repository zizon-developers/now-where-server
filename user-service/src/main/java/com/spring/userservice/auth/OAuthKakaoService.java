package com.spring.userservice.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spring.userservice.auth.exception.KakaoFriendsException;
import com.spring.userservice.service.UserService;
import com.spring.userservice.vo.ResponseOrder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthKakaoService {
    private RestTemplate restTemplate = new RestTemplate();
    private final Environment evn;
    private final UserService userService;

    public void getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
//            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=utf-8");

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=867de5dcb061c1d22c4188893654cddb"); // TODO REST_API_KEY 입력
            sb.append("&client_secret=bXXA2RVw5KmpoVjrHns8xGvOHZXvSizK"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:8000/user-service/oauth/callback/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
//            JsonParser parser = new JsonParser();
//            JsonElement element = parser.parse(result);
            JsonElement element = JsonParser.parseString(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("access_token ={}",access_Token);
            log.info("refresh_toke ={}",refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        createKakaoUser(access_Token);
    }

    private void createKakaoUser(String token) {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode ={}",responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body ={} ",result);

            //Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(result);

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            log.info("id ={}" + id);
            log.info("email ={}" + email);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getKakaoFriends(String accessToken) {
        log.info(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String reqURL = "https://kapi.kakao.com/v1/api/talk/friends";
        URI targetUrl = UriComponentsBuilder
                .fromUriString(reqURL) // 기본 URL
                .queryParam("limit", 15)
                .build()
                .encode(StandardCharsets.UTF_8) // 인코딩
                .toUri();

        ResponseEntity<Map> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new KakaoFriendsException("Failed to retrieve Kakao friends. Status code: " + response.getStatusCode());
        }
    }
}

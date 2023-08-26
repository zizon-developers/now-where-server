package com.spring.nowwhere.api.v1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.nowwhere.api.v1.entity.user.dto.RequestLogin;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private TokenProvider tokenProvider;
    private Environment env;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   Environment env,
                                   TokenProvider tokenProvider) {
        super.setAuthenticationManager(authenticationManager);
        this.tokenProvider = tokenProvider;
        this.env = env;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        //POST요청은 request 파라미터로 전달받을 수 없어서 inputStream으로 처리 /login
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String token = tokenProvider.generateJwtAccessToken(principalDetails.getUser());

        response.addHeader(HttpHeaders.AUTHORIZATION, token);
        response.addHeader("userId", principalDetails.getUser().getId()+"");
    }
}

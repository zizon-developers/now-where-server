package com.spring.nowwhere.api.v1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.nowwhere.api.v1.entity.User;
import com.spring.nowwhere.api.v1.entity.UserRepository;
import com.spring.nowwhere.api.v1.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.jwt.TokenProvider;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenRedisRepository;
import com.spring.nowwhere.api.v1.security.exception.LogoutTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 인가
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 BasicAuthenticationFilter를 무조건 타게 되어있다.
// 만약에 권한이 인증이 필요한 주소가 아니라면 이필터를 안탄다.
@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    private TokenProvider tokenProvider;
    private LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               UserRepository userRepository,
                               TokenProvider tokenProvider,
                               LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.logoutAccessTokenRedisRepository = logoutAccessTokenRedisRepository;
    }

    //인증이나 권한이 필요한 주소요청이 있을때 거침
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(JwtProperties.ACCESS_HEADER_STRING);
        //헤더가 있는지 확인
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        log.info("header:{}", header);
        String token = header.replace(JwtProperties.TOKEN_PREFIX, "");


        String email = "";
        try {
            email = tokenProvider.getUserEmailFromAccessToken(token);
            if (logoutAccessTokenRedisRepository.findByEmail(email).isPresent()){
                throw new LogoutTokenException("Logout된 토큰 입니다.");
            }

            // 이메일이 유효하면, 다음 필터로 진행
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("token에 해당하는 유저가 없습니다."));

            PrincipalDetails principalDetails = new PrincipalDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails,
                    null,
                    principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // JWT 토큰이 만료된 경우
            handleAuthenticationExceptionMessage(request, response, e, HttpStatus.UNAUTHORIZED.value(), "TOKEN-TIMEOUT-EX");
        } catch (LogoutTokenException e) {
            handleAuthenticationExceptionMessage(request, response, e, HttpStatus.UNAUTHORIZED.value(), "LOGOUT-EX");
        } catch (Exception e) {
            handleAuthenticationExceptionMessage(request, response, e, HttpStatus.FORBIDDEN.value(), "ANY-EX");
        }
    }

    private static void handleAuthenticationExceptionMessage(HttpServletRequest request, HttpServletResponse response, Exception e, int setStatusValue, String code) throws IOException {
        response.setStatus(setStatusValue);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .uri(request.getRequestURI())
                .code(code)
                .message(e.getMessage())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String errorJson = mapper.writeValueAsString(errorResponse);

        writer.write(errorJson);
        writer.flush();
    }
}

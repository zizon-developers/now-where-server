package com.spring.nowwhere.api.v1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .uri(request.getRequestURI())
                .code("TOKEN-NOT-EX")
                .message(authException.getMessage())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String errorJson = mapper.writeValueAsString(errorResponse);


        writer.write(errorJson);
        writer.flush();
    }
}
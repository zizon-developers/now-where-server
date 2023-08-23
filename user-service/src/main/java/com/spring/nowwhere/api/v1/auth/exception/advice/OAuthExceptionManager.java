package com.spring.nowwhere.api.v1.auth.exception.advice;

import com.spring.nowwhere.api.v1.auth.exception.DuplicateUserException;
import com.spring.nowwhere.api.v1.auth.exception.OauthKakaoApiException;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.exception.LogoutTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice(basePackages = "com.spring.nowwhere.api.v1.auth")
@RequiredArgsConstructor
public class OAuthExceptionManager {

    private final ResponseApi responseApi;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity HttpClientErrorExceptionBadRequestHandler (HttpClientErrorException.BadRequest  e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("KAKAO-400-EX", e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity HttpClientErrorExceptionUnauthorizedHandler (HttpClientErrorException.Unauthorized  e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("KAKAO-401-EX", e.getResponseBodyAsString(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity HttpClientErrorExceptionForbiddenHandler (HttpClientErrorException.Forbidden  e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("KAKAO-403-EX", e.getResponseBodyAsString(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OauthKakaoApiException.class)
    public ResponseEntity OauthKakaoApiExceptionHadnler (OauthKakaoApiException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("K-CONTROLLER-EX", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity usernameNotFoundHandler (UsernameNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("USER-NOT-EX", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity duplicateUserExceptionHadnler (DuplicateUserException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("USER-DUPE-EX", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LogoutTokenException.class)
    public ResponseEntity OauthKakaoApiExceptionHadnler (LogoutTokenException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("LOGOUT-EX", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("HARD-EX", "[server error] "+e.getClass().getName(), HttpStatus.CONFLICT);
    }
}

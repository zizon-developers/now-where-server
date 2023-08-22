package com.spring.nowwhere.api.v1.auth.exception.advice;

import com.spring.nowwhere.api.v1.auth.ErrorResult;
import com.spring.nowwhere.api.v1.auth.exception.DuplicateUserException;
import com.spring.nowwhere.api.v1.auth.exception.OauthKakaoApiException;
import com.spring.nowwhere.api.v1.security.exception.LogoutTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice(basePackages = "com.spring.nowwhere.api.v1.auth")
public class OAuthExceptionManager {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResult usernameNotFoundHandler (UsernameNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("USER-NOT-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ErrorResult HttpClientErrorExceptionBadRequestHandler (HttpClientErrorException.BadRequest  e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("KAKAO-400-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ErrorResult HttpClientErrorExceptionUnauthorizedHandler (HttpClientErrorException.Unauthorized  e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("KAKAO-401-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ErrorResult HttpClientErrorExceptionForbiddenHandler (HttpClientErrorException.Forbidden  e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("KAKAO-403-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateUserException.class)
    public ErrorResult duplicateUserExceptionHadnler (DuplicateUserException e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("USER-DUPE-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OauthKakaoApiException.class)
    public ErrorResult OauthKakaoApiExceptionHadnler (OauthKakaoApiException e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("KAKAO-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LogoutTokenException.class)
    public ErrorResult OauthKakaoApiExceptionHadnler (LogoutTokenException e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("LOGOUT-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }
}

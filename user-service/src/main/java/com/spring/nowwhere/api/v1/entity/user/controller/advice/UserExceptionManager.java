package com.spring.nowwhere.api.v1.entity.user.controller.advice;

import com.spring.nowwhere.api.v1.auth.exception.RefreshTokenNotFoundException;
import com.spring.nowwhere.api.v1.entity.bet.exception.TimeValidationException;
import com.spring.nowwhere.api.v1.entity.user.exception.DuplicateRemittanceIdException;
import com.spring.nowwhere.api.v1.entity.user.exception.DuplicateUsernameException;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.exception.LogoutTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.spring.nowwhere.api.v1.entity.user.controller")
@RequiredArgsConstructor
public class UserExceptionManager {

    private final ResponseApi responseApi;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity usernameNotFoundHandler (UsernameNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("USER-NOT-EX", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LogoutTokenException.class)
    public ResponseEntity logoutTokenExceptionHadnler (LogoutTokenException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("LOGOUT-EX", e.getMessage(), HttpStatus.CONFLICT);
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity duplicateUsernameExceptionHadnler (DuplicateUsernameException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("DUPLICATE-NAME-EX", e.getMessage(), HttpStatus.CONFLICT);
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateRemittanceIdException.class)
    public ResponseEntity duplicateRemittanceIdExceptionHadnler (DuplicateRemittanceIdException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("DUPLICATE-PAY-EX", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity MissingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("REQ-EX", "[server error] " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("HARD-EX", "[server error] " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

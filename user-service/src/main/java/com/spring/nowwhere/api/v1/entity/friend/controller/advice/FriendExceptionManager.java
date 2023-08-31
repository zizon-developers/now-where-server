package com.spring.nowwhere.api.v1.entity.friend.controller.advice;

import com.spring.nowwhere.api.v1.entity.bet.exception.TimeValidationException;
import com.spring.nowwhere.api.v1.entity.friend.exception.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.friend.exception.FriendNotFoundException;
import com.spring.nowwhere.api.v1.entity.friend.exception.FriendRequestPendingException;
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

@Slf4j
@RestControllerAdvice(basePackages = "com.spring.nowwhere.api.v1.entity.friend.controller")
@RequiredArgsConstructor
public class FriendExceptionManager {

    private final ResponseApi responseApi;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity usernameNotFoundHandler (UsernameNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("USER-NOT-EX", e.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyFriendsException.class)
    public ResponseEntity alreadyFriendsExceptionHandler (AlreadyFriendsException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("ALREADY-EX", e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FriendNotFoundException.class)
    public ResponseEntity FriendNotFoundExceptionHandler (FriendNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("FRIEND-NOT-EX", e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FriendRequestPendingException.class)
    public ResponseEntity friendRequestPendingExceptionHandler (FriendRequestPendingException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("F-PENDING-EX", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LogoutTokenException.class)
    public ResponseEntity logoutTokenExceptionHadnler (LogoutTokenException e){
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("LOGOUT-EX", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return responseApi.fail("HARD-EX", "[server error] " + e.getMessage(), HttpStatus.CONFLICT);
    }
}

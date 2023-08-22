package com.spring.nowwhere.api.v1.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ResponseApi<T> {

    @Getter
    @Builder
    private static class Body<T> {

        private int state;
        private T data;
        private String massage;
        private Object error;
    }

    public ResponseEntity<Body<T>> success(T data, String msg, HttpStatus status) {
        Body<T> body = Body.<T>builder()
                .state(status.value())
                .data(data)
                .massage(msg)
                .error(Collections.emptyList())
                .build();
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Body<T>> success(String msg) {
        return success(null, msg, HttpStatus.OK);
    }
    public ResponseEntity<Body<T>> success(T data) {
        return success(data, null, HttpStatus.OK);
    }
    public ResponseEntity<T> success() {
        return (ResponseEntity<T>) success(null, null, HttpStatus.OK);
    }

    public ResponseEntity<Body<T>> fail(T data, String msg, HttpStatus status) {
        Body<T> body = Body.<T>builder()
                .state(status.value())
                .data(data)
                .massage(msg)
                .error(Collections.emptyList())
                .build();
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Body<T>> fail(String msg, HttpStatus status) {
        return fail(null, msg, status);
    }
}
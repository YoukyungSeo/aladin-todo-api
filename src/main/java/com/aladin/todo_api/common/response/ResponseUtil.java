package com.aladin.todo_api.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static ResponseEntity<Response> response(String message, Object data) {
        return ResponseEntity.ok(new Response(HttpStatus.OK.value(), message, data));
    }

}

package com.demo.employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NameRequireException extends ResponseStatusException{

    public NameRequireException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

}

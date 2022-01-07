package com.demo.employee.exception;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {

    private HttpStatus httpstatus;
    private String resources;
    private String message;

}

package com.demo.employee.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DataNotFound.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public Mono<ErrorResponse> onResourceFound(DataNotFoundException exception) {
        log.error("No resource found exception occurred: {} ", exception.getMessage());

        ErrorResponse response = new ErrorResponse();
        response.getErrors().add(
                new Error(
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        exception.getMessage()));

        return Mono.just(response);
    }
}


package com.demo.employee.exception;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ErrorResponse {

    private List<Error> errors = new ArrayList<>();

}

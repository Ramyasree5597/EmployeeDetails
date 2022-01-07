package com.demo.employee.util;

import com.demo.employee.model.Employee;

public class EmployeeUtil {

    private static Employee employee;

    public static Employee getInstace() {


        employee.setId("1");
        employee.setName("IBM");
        employee.setSalary("15000");

        return employee;

    }
}
package com.demo.employee.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.employee.model.Employee;


@Repository
public interface EmployeeRepository extends ReactiveMongoRepository<Employee,String> {

}

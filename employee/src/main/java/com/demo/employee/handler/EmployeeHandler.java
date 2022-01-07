package com.demo.employee.handler;

import com.demo.employee.exception.DataNotFound;
import com.demo.employee.exception.DataNotFoundException;
import com.demo.employee.exception.NameRequireException;
import com.demo.employee.model.Employee;
import com.demo.employee.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class EmployeeHandler {

    private EmployeeRepository employeeRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ServerResponse.ok().body(employeeRepository.findAll().switchIfEmpty(Mono.defer(() -> Mono.error(dataNotException("Data Not Found")))), Employee.class);
    }

    public Mono<ServerResponse> getEmployeeByID(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return ServerResponse.ok().body(employeeRepository.findById(id).switchIfEmpty(Mono.defer(() -> Mono.error(createDataException(id))))
                , Employee.class);
    }

    public Mono<ServerResponse> deleteEmployeeByID(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        Mono<Employee> personMono = employeeRepository.findById(id);
        return personMono.flatMap( employee -> ServerResponse.ok()
                .body(employeeRepository.deleteById(id)
                        , Employee.class)).switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> updateEmployee(ServerRequest serverRequest) {

        String employeeId = serverRequest.pathVariable("id");
        Mono<Employee> updateEmployee = serverRequest.bodyToMono(Employee.class).flatMap((employee) -> {
            Mono<Employee> empMono = employeeRepository.findById(employeeId.trim()).flatMap(currentEmp -> {
                currentEmp.setName(employee.getName());
                currentEmp.setSalary(employee.getSalary());

                return employeeRepository.save(currentEmp);
            });
            return empMono;
        });
        return updateEmployee.flatMap(
                        employee -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(employee)))
                .switchIfEmpty(Mono.defer(() -> Mono.error(createDataException(employeeId))));

    }

    public Mono<ServerResponse> saveEmployee(ServerRequest serverRequest) {

        Employee emp1 = new Employee();
        emp1.setName("Dummy");
        emp1.setSalary("Vijayawada");
        final Mono<Employee> emp = serverRequest.bodyToMono(Employee.class);
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(emp.flatMap(employeeRepository::save).onErrorResume(e -> Mono.just(emp1)), Employee.class));
    }

    public Mono<ServerResponse> getName(ServerRequest serverRequest) {

        return sayHandler(serverRequest).onErrorReturn("Hello Stranger")
                .flatMap(s -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(s));
    }

    public Mono<ServerResponse> getNameOnErrorResume(ServerRequest request) {
        return ServerResponse.ok()
                .body(sayHandler(request)
                        .onErrorResume(e ->
                                Mono.error(new NameRequireException(
                                        HttpStatus.BAD_REQUEST, "Enter name value", e))), String.class);
    }

    public Mono<String> sayHandler(ServerRequest serverRequest) {

        try {
            return Mono.just(new String("Hello " + serverRequest.queryParam("name").get()));
        } catch (Exception e) {
            return Mono.error(e);
        }

    }

    private Throwable createDataException(String id) {
        throw new DataNotFoundException( id);
    }

    private Throwable dataNotException(String message){
        throw new DataNotFound(message);
    }
}

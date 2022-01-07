package com.demo.employee.router;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.demo.employee.handler.EmployeeHandler;
import com.demo.employee.model.Employee;
import com.demo.employee.repository.EmployeeRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.assertj.core.api.Assertions;

@WebFluxTest
@ContextConfiguration(classes = { RouterConfigTest.class, EmployeeHandler.class })
@RunWith(SpringRunner.class)
@TestMethodOrder(OrderAnnotation.class)
public class RouterConfigTest {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private WebTestClient webTestClient;


    @Before
    public void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();

    }

    @Test
    @Order(2)
    public void findByIDTest() {
        Employee emp = new Employee("1", "IBM", "20000");

        Mono<Employee> UserMono = Mono.just(emp);
        when(employeeRepository.findById("1")).thenReturn(UserMono);

        webTestClient.get().uri(builder -> builder.path("/{id}").build("1")).accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectBody(Employee.class).value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("1");
                    Assertions.assertThat(response.getName()).isEqualTo("IBM");
                });

    }
    @Test
    @Order(1)
    public void saveEmployee() {
        Employee emp = new Employee("1", "IBM-Test", "20000");

        Mono<Employee> UserMono = Mono.just(emp);
        when(employeeRepository.save(any())).thenReturn(UserMono);

        webTestClient.post().uri(builder -> builder.path("/save").build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(UserMono),Employee.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Employee.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("1");
                    Assertions.assertThat(response.getName()).isEqualTo("IBM-Test");
                    Assertions.assertThat(response.getSalary()).isEqualTo("20000");
                });

    }

    @Test
    @Order(3)
    public void updateByIDTest() {
        Employee emp = new Employee("1", "IBM", "20000");

        when(employeeRepository.findAll()).thenReturn(
                Flux.just(emp));
        when(employeeRepository.findById("1")).thenReturn(
                Mono.just(emp));
        emp.setName("DXC");
        when(employeeRepository.save(emp)).thenReturn(Mono.just(emp));



        webTestClient.put().uri(builder -> builder.path("/updateEmployee/{id}").build("1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(emp),Employee.class)
                .exchange().expectStatus().isOk().expectBody(Employee.class).value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("1");
                    Assertions.assertThat(response.getName()).isEqualTo("DXC");
                });

    }
    @Test
    @Order(4)
    public void deleteByIDTest() {
        Employee emp = new Employee("1", "IBM", "20000");

        Mono<Employee> UserMono = Mono.just(emp);
        when(employeeRepository.findById("1")).thenReturn(UserMono);
        when(employeeRepository.deleteById("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/deleteEmployee/{id}","1")
                .exchange().expectStatus().isOk();

    }
    @Test
    @Order(5)
    public void delete_Not_Found_Test() {
        Employee emp = new Employee("1", "IBM", "20000");

        when(employeeRepository.findById("1")).thenReturn(Mono.empty());
        when(employeeRepository.deleteById("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/deleteEmployee/{id}","1")
                .exchange().expectStatus().isNotFound();

    }

    @Test
    @Order(6)
    public void getEmpByID_Not_Found_Test() {

        when(employeeRepository.findById("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/deleteEmployee/{id}","1")
                .exchange().expectStatus().isNotFound();

    }

    @Test
    @Order(6)
    public void getUpdateID_Not_Found_Test() {

        when(employeeRepository.findById("1")).thenReturn(Mono.empty());

        webTestClient.put().uri("/updateEmployee/{id}","1")
                .exchange().expectStatus().isNotFound();

    }

    @Test
    @Order(7)
    public void testFetchAllEmployees(){
        Employee  employeeEntity1 = Employee.builder().id("1").name("test").salary("100").build();
        Employee employeeEntity2 = Employee.builder().id("2").name("abc").salary("200").build();

        when(employeeRepository.findAll()).thenReturn(Flux.just(employeeEntity1,employeeEntity2));
        Flux<Employee> responseBody = webTestClient.get().uri("/listEmployee")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Employee.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(new Employee("1", "test","100"))
                .expectNext(new Employee("2","abc","200"))
                .verifyComplete();
    }

}

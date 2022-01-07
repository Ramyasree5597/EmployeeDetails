package com.demo.employee.router;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import com.demo.employee.handler.EmployeeHandler;

    @Configuration
    @EnableWebFlux
    public class EmployeeRouter {

        @Bean
        RouterFunction<?> routerFunction(EmployeeHandler routerHandlers){

            return RouterFunctions.route(RequestPredicates.GET("/listEmployee"),routerHandlers::findAll)
                    .andRoute(RequestPredicates.GET("/{id}"),routerHandlers::getEmployeeByID)
                    .andRoute(RequestPredicates.POST("/save"),routerHandlers::saveEmployee)
                    .andRoute(RequestPredicates.DELETE("/deleteEmployee/{id}"),routerHandlers::deleteEmployeeByID)
                    .andRoute(RequestPredicates.PUT("/updateEmployee/{id}").and(accept(APPLICATION_JSON)),routerHandlers::updateEmployee);
        }

        @Bean
        RouterFunction<?> errorRouteFunction(EmployeeHandler routerHandlers){

            return RouterFunctions.route(RequestPredicates.GET("/error"),routerHandlers::getName)
                    .andRoute(RequestPredicates.GET("/getNameErr"), routerHandlers::getNameOnErrorResume);

        }
    }


package com.example.controller.impl;

import com.example.controller.IRestEmployeeController;
import com.example.dto.DtoEmployee;
import com.example.service.IEmpolyeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/employee")
public class RestEmployeeControllerImpl implements IRestEmployeeController {

    @Autowired
    private IEmpolyeeService empolyeeService;

    @GetMapping("/list/{id}")
    @Override
    public DtoEmployee getEmployeeById(@PathVariable(value = "id") Long id) {
        return empolyeeService.getEmployeeById(id);
    }
}

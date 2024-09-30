package com.example.service.impl;

import com.example.dto.DtoDepartment;
import com.example.dto.DtoEmployee;
import com.example.model.Department;
import com.example.model.Employee;
import com.example.repository.EmployeeRepository;
import com.example.service.IEmpolyeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements IEmpolyeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public DtoEmployee getEmployeeById(Long id) {

        DtoEmployee dtoEmployee = new DtoEmployee();
        DtoDepartment dtoDepartment = new DtoDepartment();

        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isEmpty()){
            return null;
        }

        Employee employee = optional.get();
        Department department = employee.getDepartment();

        BeanUtils.copyProperties(employee, dtoEmployee);
        BeanUtils.copyProperties(department, dtoDepartment);

        dtoEmployee.setDepartment(dtoDepartment);

        return dtoEmployee;
    }
}

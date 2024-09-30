package com.example.controller;

import com.example.dto.DtoEmployee;
import org.springframework.web.bind.annotation.PathVariable;

public interface IRestEmployeeController {

    DtoEmployee getEmployeeById( Long id);
}

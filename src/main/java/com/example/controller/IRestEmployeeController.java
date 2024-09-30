package com.example.controller;

import com.example.dto.DtoEmployee;

public interface IRestEmployeeController {

    DtoEmployee getEmployeeById( Long id);
}

package com.example.controller;

import com.example.dto.DtoEmployee;
import com.example.model.RootEntity;

public interface IRestEmployeeController {

    RootEntity<DtoEmployee> getEmployeeById(Long id);
}

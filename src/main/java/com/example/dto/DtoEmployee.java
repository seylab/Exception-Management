package com.example.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoEmployee {

    private Long id;

    private String name;

    private DtoDepartment department;

}

package com.example;

import com.example.dto.DtoEmployee;
import com.example.service.IEmpolyeeService;
import com.example.starter.ExceptionManagementStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ExceptionManagementStarter.class})
class ExceptionManagementStarterTests {

	@Autowired
	private IEmpolyeeService empolyeeService;
	@Test
	public void testGetEmployeeById(){
		DtoEmployee dtoEmployee= empolyeeService.getEmployeeById(11L);
		if(dtoEmployee!=null){
			System.out.println("Name " + dtoEmployee.getName());
		}
	}

}

package com.kclm.xsap.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployeeServiceTest {

	@Autowired
	EmployeeService employeeService;
	
	//操作web界面时，已测
	@Test
	public void login() {
	}
	
	//操作web界面时，已测
	@Test
	public void register() {
		
	}
	
	//短信验证码方式，待编写
	@Test
	public void resetPwdByPhone() {
		
	}

	//操作web界面时，已测
	@Test
	public void resetPwdByEmail() {
		
	}

	//操作web界面时，已测
	@Test
	public void updatePassword() {
		
	}
	
	//操作web界面时，已测
	@Test
	public void resetPassward() {
		
	}
	
	//操作web界面时，已测
	@Test
	public void update() {
		
	}

	//操作web界面时，已测
	@Test
	public void findByUser() {
		
	}

	//操作web界面时，已测
	@Test
	public void findById() {
		
	}
}

package com.kclm.xsap.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.mapper.TEmployeeMapper;

@SpringBootTest
public class TeacherServiceTest {

	@Autowired
	TeacherService teacherService;
	
	@Autowired
	TEmployeeMapper employeeMapper;
	
	@Test
	public void save() {
		TEmployee emp = new TEmployee();
		emp.setName("小玉");
		emp.setPhone("12321");
		emp.setRoleEmail("2346879578@qq.com");
		emp.setSex("女");
		emp.setBirthday(LocalDate.now());
		emp.setIntroduce("刚入职的萌新老师");
		emp.setNote("能力综合");
		teacherService.save(emp);
	}
	
	@Test
	public void listClassView() {
		List<ClassRecordDTO> listClassRecord = employeeMapper.listClassView(2L);
		for (ClassRecordDTO classRecordDTO : listClassRecord) {
			System.out.println(classRecordDTO);
		}
	}
	
	@Test
	public void deleteById() {
		teacherService.deleteById(2L);
	}
	
	@Test
	public void update() {
		TEmployee emp = new TEmployee();
		emp.setId(3L);
		emp.setName("小玉");
		teacherService.update(emp);
	}
	
	@Test
	public void findAll() {
		List<TEmployee> list = teacherService.findAll();
		for (TEmployee emp : list) {
			System.out.println("---"+ emp);
		}
	}

	@Test
	public void findAllByPage() {
		List<TEmployee> list = teacherService.findAllByPage(1, 3);
		for (TEmployee emp : list) {
			System.out.println("---"+ emp);
		}
	}

	@Test
	public void getAnalysis() {
		TEmployee emp = teacherService.getAnalysis(1L);
		System.out.println("当前老师："+ emp);
	}

	//---------涉及到convert
	@Test
	public void listClassRecord() {
		List<ClassRecordDTO> list = teacherService.listClassRecord(2L);
		for (ClassRecordDTO classed : list) {
			System.out.println("-------");
			System.out.println(classed);
			System.out.println("-------");
		}
	}
}

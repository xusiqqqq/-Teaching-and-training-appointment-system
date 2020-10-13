package com.kclm.xsap.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.entity.TScheduleRecord;


@SpringBootTest
public class CourseScheduleTest {


	@Autowired
	private CourseScheduleService scheduleService;
	
	//新增排课记录
	@Test
	public void save() {
		TScheduleRecord schedule = new TScheduleRecord();
		schedule.setCourseId(3L);
		schedule.setTeacherId(3L);
		schedule.setStartDate(LocalDate.of(2020, 10, 6));
		schedule.setClassTime(LocalTime.now());
		scheduleService.save(schedule);
		}
	
	//删除指定排课记录
	@Test
	public void deleteById() {
		scheduleService.deleteById(2L);
	}

	//查询一段时间里的所有排课记录
	@Test
	public void listSchedule() {
		LocalDate startDate = LocalDate.of(2020, 9, 8);
		LocalDate endDate = LocalDate.of(2020, 9, 9);
		List<CourseScheduleDTO> listSchedule = scheduleService.listSchedule(startDate, endDate);
		for (CourseScheduleDTO schedule : listSchedule) {
			System.out.println("---: " + schedule);
		}
	}

	//------涉及到convert
	@Test
	public void findById() {
		CourseScheduleDTO schedule = scheduleService.findById(1L);
		System.out.println("排课计划："+schedule);
	}
}

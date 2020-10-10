package com.kclm.xsap.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/course")
public class CourseController {

	//文件夹名
	private static final String folderName = "course";
	
	
	/* 团课课程操作 */
	@RequestMapping("/x_course_list.do")
	public String x_course_list() {
		
		return folderName + "/x_course_list";
	}
	
	/* 团课课程表操作 */
	@RequestMapping("/x_course_schedule.do")
	public String x_course_schedule() {
		
		return folderName + "/x_course_schedule";
	}
	
	
	/* 团课预约设置操作 */
	@RequestMapping("/x_course_reservation.do")
	public String x_course_reservation() {
		
		return folderName + "/x_course_reservation";
	}
	
}

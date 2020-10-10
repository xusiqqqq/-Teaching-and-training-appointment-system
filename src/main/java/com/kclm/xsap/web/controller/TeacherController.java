package com.kclm.xsap.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class TeacherController {

	//文件夹名
	private static final String folderName = "employee";
	
	@RequestMapping("/x_teacher_list.do")
	public String x_teacher_list() {
		return folderName + "/x_teacher_list";
	}
	
}

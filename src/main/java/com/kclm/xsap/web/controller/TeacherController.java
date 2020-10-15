package com.kclm.xsap.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.service.TeacherService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/employee")
@Slf4j
public class TeacherController {

	//文件夹名
	private static final String folderName = "employee";

	@Autowired
	TeacherService teacherService;
	
	/* 页面跳转 */
	
	//=》添加老师
	@RequestMapping("/x_teacher_add.do")
	public String x_teacher_add() {
		return folderName + "/x_teacher_add";
	}
	
	//=》编辑老师
	@RequestMapping("/x_teacher_update.do")
	public String x_teacher_update() {
		return folderName + "/x_teacher_update";
	}
	
	/* 页面数据处理 */
	//=》老师概览页
	@RequestMapping("/x_teacher_list.do")
	public String x_teacher_list() {
		List<TEmployee> empList = teacherService.findAll();
		log.debug("-----全部教师信息：" + empList);
		
		
		return folderName + "/x_teacher_list";
	}

	//添加老师
	@RequestMapping("/teacherAdd.do")
	public String teacherAdd(@RequestBody TEmployee emp) {
		log.debug("---前端数据" + emp);
		
		return folderName + "/x_teacher_list";
	}
	
	//编辑老师信息
	@RequestMapping("/teacherEdit.do")
	public String teacherEdit(@RequestBody TEmployee emp) {
		log.debug("---前端id" + emp);
		
		return folderName + "/x_teacher_list";
	}
	
	//老师信息详情
	@RequestMapping("/teacherDetail.do")
	public String teacherDetail(Long teacherId) {
		log.debug("---前端id" + teacherId);
		
		return folderName + "/x_teacher_list_data";
	}
	
	
}

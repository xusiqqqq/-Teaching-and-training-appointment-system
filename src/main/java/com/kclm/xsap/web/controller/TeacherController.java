package com.kclm.xsap.web.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.service.EmployeeService;
import com.kclm.xsap.service.TeacherService;
import com.kclm.xsap.utils.FileUploadUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/employee")
@Slf4j
public class TeacherController {

	
	//文件夹名
	private static final String folderName = "employee/";

	@Autowired
	TeacherService teacherService;
	
	@Autowired
	EmployeeService employeeService;
	
	/*======= 页面跳转======== */
	
	//=》老师概览页
	@RequestMapping("/x_teacher_list.do")
	public String x_teacher_list() {
		return folderName + "x_teacher_list";
	}
		
	//=》添加老师
	@RequestMapping("/x_teacher_add.do")
	public String x_teacher_add() {
		return folderName + "x_teacher_add";
	}
	
	//=》编辑老师
	@RequestMapping("/x_teacher_update.do")
	public String x_teacher_update(Long id,Model model) {
		TEmployee emp = employeeService.findById(id);
		model.addAttribute("teacherMsg", emp);
		model.addAttribute("birthdayStr",null);
			if(emp.getBirthday() != null) {
				model.addAttribute("birthdayStr", emp.getBirthday().toString());			
			}			
		return folderName + "x_teacher_update";
	}
	
	//=》老师信息详情
	@RequestMapping("/x_teacher_list_data.do")
	public String x_teacher_list_data(Long id,Model model) {
		model.addAttribute("ID", id);
		return folderName + "x_teacher_list_data";
	}
	
	/* =======页面数据处理======= */
	
	//老师概览页 - 预加载
	@RequestMapping("/teacherList.do")
	@ResponseBody
	public List<TEmployee> teacherList() {
		List<TEmployee> empList = teacherService.findAll();
		return empList;
	}

	//老师信息详情 - 预加载
	@RequestMapping("/teacherDetail.do")
	@ResponseBody
	public TEmployee teacherDetail(Long tid) {
		//老师基本信息
		TEmployee emp = teacherService.getAnalysis(tid);
		return emp;
	}
	
	//老师上课记录 - 预加载
	@RequestMapping("/teacherClassRecord.do")
	@ResponseBody
	public List<ClassRecordDTO> teacherClassRecord(Long tid){
		List<ClassRecordDTO> classList = teacherService.listClassRecord(tid);
		return classList;
	}
	
	//添加老师
	@RequestMapping("/teacherAdd.do")
	public String teacherAdd(TEmployee emp,Model model) {
		System.out.println("---------");
		System.out.println(emp);
		System.out.println("---------");
		
		//手机号已存在的提示
		model.addAttribute("CHECK_PHONE_EXIST", false);

		TEmployee user = null;
		if(emp.getPhone().length() > 0) {
			user = employeeService.findByUser(emp.getPhone());
		}
		if(user != null) {
			model.addAttribute("CHECK_PHONE_EXIST", true);
			return "forward:x_teacher_add.do";
		}
		
		//数据录入
		teacherService.save(emp);
		
		return "forward:x_teacher_list.do";
	}
	
	//编辑老师信息
	@RequestMapping("/teacherEdit.do")
	public String teacherEdit(TEmployee emp,Model model) {
		 Long id = emp.getId();;
		log.debug("---id " + id);		
		log.debug("---前端表单数据 " + emp);
		TEmployee oldEmp = employeeService.findById(id);
		emp.setRoleName(oldEmp.getRoleType() == 1 ? "超级管理员":"普通管理员");
		emp.setCreateTime(oldEmp.getCreateTime());
		emp.setLastModifyTime(LocalDateTime.now());
		emp.setVersion(oldEmp.getVersion());
		//判断新输入的手机号是否已存在
		TEmployee checkPhone = employeeService.findByUser(emp.getPhone());
		if(checkPhone != null) {
			//校检手机号的提示信息
			model.addAttribute("CHECK_PHONE_EXIST", false);
			//当前的手机号有变化且已存在时
			if(!checkPhone.getPhone().equals(oldEmp.getPhone()) ) {
				model.addAttribute("CHECK_PHONE_EXIST", true);
				//返回原值
				return "forward:x_teacher_update.do";
			}
		}	
		
		teacherService.update(emp);				
		
		return "forward:x_teacher_list.do";
	}
	
	//删除单个记录
	@RequestMapping("/deleteOne.do")
	public String deleteOne(Long id) {
		System.out.println("delete id : " + id);
			teacherService.deleteById(id);
		return "forward:x_teacher_list.do";
	}
	
	//修改用户信息
		//1、修改图像信息
		@RequestMapping(value = "/modifyUserImg.do",produces = {"application/json;charset=UTF-8"})
		@ResponseBody
		public TEmployee modifyUserImg(@RequestParam("avatarFile") MultipartFile avatarFile,Long id) {
			System.out.println("=====avatar: " + avatarFile);
			System.out.println("=====id: " + id);
			//查询到当前的员工信息
			TEmployee oldEmp = employeeService.findById(id);
			if(!avatarFile.isEmpty()) {
				//上传文件
				String fileName;
				try {
					fileName = FileUploadUtil.uploadFiles(avatarFile);
					//设置图片全名
					oldEmp.setAvatarUrl(fileName);
				} catch (Exception e) {
					System.out.println("-----------图片信息有误！-----------");
					e.printStackTrace();
				}
			}
			TEmployee employee = employeeService.update(oldEmp);
			System.out.println("---------profile---------");
			System.out.println(employee);
			System.out.println("---------");
			return employee;
		}
	
		//老师搜索
		@ResponseBody
		@RequestMapping("/toSearch.do")
		public Map<String, List<TEmployee>> toSearch() {
			List<TEmployee> teacherList = new ArrayList<>();
			teacherList = teacherService.findAll();
			
			Map<String , List<TEmployee>> search = new HashMap<>();
			search.put("value", teacherList);
			return search;
		}
}

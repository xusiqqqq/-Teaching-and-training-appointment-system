/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.service.EmployeeService;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-04 9:10
 * @Description TODO
 */
@Controller
@RequestMapping("/user")
public class EmployeeController {
	
	//调用service层
	@Autowired
	private EmployeeService employeeService;
	
	@RequestMapping("/toLogin")
	public String welcome() {
		return "x_login";
	}
	
	/**
	 *  登录
	 * @param userName
	 * @param pwd
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping("/login.do")
	public String login(String username,String password,Model model,HttpSession session) {
		TEmployee loginUser = employeeService.login(username, password);
		model.addAttribute("USER_NOT_EXIST", false);					
		if(loginUser == null) {
			model.addAttribute("USER_NOT_EXIST", true);			
			return "x_login";
		}
		model.addAttribute("userInfo", loginUser);
		session.setAttribute("LOGIN_USER", loginUser);
		return "index";
	}
	
	/**
	 * 	退出登录
	 * @param session
	 * @return
	 */
	@RequestMapping("/logout.do")
	public String logout(HttpSession session) {
		session.invalidate();
		return "x_login";
	}

	@RequestMapping("/toRegister.do")
	public String toRegister() {
		return "x_register";
	}
	
	@RequestMapping("/register.do")
	public String register(String userName,String password,String pwd2,Model model) {
		model.addAttribute("CHECK_PWD_ERROR",false);
		if(!password.equals(pwd2)) {
			model.addAttribute("CHECK_PWD_ERROR",true);
			return "x_register";
		}
		TEmployee emp = new TEmployee();
		emp.setRoleName(userName);
		emp.setRolePassword(password);
		//设置默认真实姓名
		emp.setName("user");
		employeeService.register(emp);
		return "x_login";
	}
	
}

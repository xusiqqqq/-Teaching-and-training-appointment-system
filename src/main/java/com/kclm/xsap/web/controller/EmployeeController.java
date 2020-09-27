/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.web.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

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
	
	/* ============一些全局设置=========== */
	
	//保存用户账号
	private String global_username = "";
	
	/* ============用作页面跳转============ */
	
	//登录界面
	@RequestMapping("/toLogin")
	public String welcome() {
		return "x_login";
	}
	
	//注册界面
	@RequestMapping("/toRegister.do")
	public String toRegister() {
		return "x_register";
	}
	
	//账户认证页面
	@RequestMapping("/toEnsureUser.do")
	public String toEnsureUser() {
		return "x_ensure_user";
	}
		
	//重置密码页面
	@RequestMapping("/gotoResetPwdPage.do")
	public String toRestPwdPage() {
		return "x_reset_passward";
	}
	
	//个人信息
	@RequestMapping("/x_profile.do")
	public String x_profile() {
		return "x_profile";
	}
	
	//修改密码
	@RequestMapping("/x_modify_password.do")
	public String x_modify_password() {
		return "x_modify_password";
	}
	
	//首页
	@RequestMapping("/toIndex.do")
	public String toIndex() {
		return "index";
	}
	/* =============用作页面数据处理=========== */
	
	//登录界面
	@RequestMapping("/login.do")
	public String login(String username,String password,Model model,HttpSession session) {
		TEmployee loginUser = employeeService.login(username, password);
		model.addAttribute("USER_NOT_EXIST", false);					
		if(loginUser == null) {
			model.addAttribute("USER_NOT_EXIST", true);			
			return "x_login";
		}
		//拿到当前用户名，供其它方法使用
		global_username=loginUser.getPhone();
		
		loginUser.setRoleName(loginUser.getRoleType() == 1 ? "超级管理员":"普通管理员");
		loginUser.setLastModifyTime(LocalDateTime.now());
		model.addAttribute("userInfo", loginUser);
		model.addAttribute("user_roleType",loginUser.getRoleName());
		//保存当前用户信息至Session域，供其它http请求使用
		session.setAttribute("LOGIN_USER", loginUser);
		return "index";
	}
	
	//退出登录
	@RequestMapping("/logout.do")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:toLogin";
	}

	//注册界面
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
	
	//根据认证的用户名，发送邮件至其邮箱
	@RequestMapping("/toResetPwd.do")
	public String toResetPassword(String userName,Model model,HttpServletRequest request) {
		TEmployee employee = employeeService.findByUser(userName.trim());
		model.addAttribute("CHECK_USER_ERROR",false);
		if(employee == null) {
			model.addAttribute("CHECK_USER_ERROR",true);
			return "x_ensure_user";
		}
		//保存当前用户账号，供其它方法使用
		global_username = employee.getPhone();
		
		//获取服务器虚拟路径
		StringBuffer bufferURL = request.getRequestURL();
		String url = bufferURL.substring(0, bufferURL.lastIndexOf("/"));
		
		String email = employee.getRoleEmail();
		System.out.println("目标邮箱："+ email);
		String content = "<a href='"+url+"/gotoResetPwdPage.do'>前往重置密码页面</a>";
		Boolean isHtml = true;
		employeeService.resetPwdByEmail(email, content, isHtml);
		System.out.println("发送成功。。。");
		return "send_mail_ok";
	}
	
	//重置当前用户的密码
	@RequestMapping("/resetPassward.do")
	public String resetPassword(String password,String pwd2,Model model) {
		model.addAttribute("CHECK_PWD_ERROR",false);
		if(!password.equals(pwd2)) {
			model.addAttribute("CHECK_PWD_ERROR",true);
			return "x_reset_passward";
		}
		//重置当前账号的密码
		boolean isOk = employeeService.resetPassward(global_username, password);
		if(!isOk) {
			System.out.println("重置密码失败");
			return "x_reset_passward";
		}
		//! 链接上带参数传递，去掉参数
		
		return "x_login";			
	}
		
	
	//修改用户信息
	//在响应数据回浏览器时，指定json类型
//	@RequestMapping(value = "/modifyUser.do",produces = "application/json")
	@RequestMapping(value = "/modifyUser.do",produces = {"application/json"})
//	@ResponseBody
	public String modifyUser(@RequestBody TEmployee emp,HttpSession session) {
		//销毁上一次的Session内容，将更新后的结果重新存入session
		session.invalidate();
		TEmployee employee = employeeService.update(emp);
		session.setAttribute("LOGIN_USER", employee);
		System.out.println("更新后：" +employee);
		return "x_profile";
	}
	
	//修改密码
	@RequestMapping("/modifyPwd.do")
	public String modifyPassward(String oldPwd,String newPwd,String pwd2,Model model) {
		model.addAttribute("CHECK_PWD_ERROR",-1);
		if(!newPwd.equals(pwd2)) {
			model.addAttribute("CHECK_PWD_ERROR",1);
			return "x_modify_password";
		}
		System.out.println("运行了1次--------------");
		boolean isOk = employeeService.updatePassword(global_username, oldPwd, newPwd);
		System.out.println(isOk +"  :-----------");
		if(!isOk) {
			//原密码不正确
			model.addAttribute("CHECK_PWD_ERROR",0);
			return "x_modify_password";
		}
		//密码修改完，重新登录
		return "redirect:logout.do";
	}
	
}

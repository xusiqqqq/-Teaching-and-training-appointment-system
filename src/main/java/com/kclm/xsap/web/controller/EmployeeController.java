/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.web.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-04 9:10
 * @Description TODO
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class EmployeeController {
	
	private static final String RESOURCE_PATH = "static/img";

	//调用service层
	@Autowired
	private EmployeeService employeeService;
	
	/* ============一些全局设置=========== */
	
	//保存用户账号
	private static String global_username = "";
	
	/* ============用作页面跳转============ */
	
	//=》登录界面
	@RequestMapping("/toLogin")
	public String welcome() {
		return "x_login";
	}
	
	//=》注册界面
	@RequestMapping("/toRegister.do")
	public String toRegister() {
		return "x_register";
	}
	
	//=》账户认证页面
	@RequestMapping("/toEnsureUser.do")
	public String toEnsureUser() {
		return "x_ensure_user";
	}
		
	//=》重置密码页面
	@RequestMapping("/gotoResetPwdPage.do")
	public String toRestPwdPage() {
		return "x_reset_passward";
	}
	
	
	//=》修改密码
	@RequestMapping("/x_modify_password.do")
	public String x_modify_password() {
		return "x_modify_password";
	}
	
	//=》首页
	@RequestMapping("/toIndex.do")
	public String toIndex() {
		return "index";
	}
	
	/* =============用作页面数据处理=========== */
	//个人信息
	@RequestMapping("/x_profile.do")
	public String x_profile(Model model) {
		System.out.println("x_profile....");
		TEmployee employee = employeeService.findByUser(global_username);
		model.addAttribute("userInfo", employee);
		return "x_profile";
	}
	
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
		//保存当前用户信息至Session域，供其它http请求使用
		session.setAttribute("LOGIN_USER", loginUser);
		return "redirect:toIndex.do";
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
		model.addAttribute("CHECK_TYPE_ERROR",-1);
		if(!password.equals(pwd2)) {
			model.addAttribute("CHECK_TYPE_ERROR",1);
			return "x_register";
		}
		//注册的用户名是否已存在
		TEmployee employee = employeeService.findByUser(userName);
		if(employee != null) {
			model.addAttribute("CHECK_TYPE_ERROR",0);
			return "x_register";
		}
		TEmployee emp = new TEmployee();
		emp.setRoleName(userName);
		emp.setRolePassword(password);
		//设置默认真实姓名
		emp.setName("user");
		employeeService.register(emp);
		return "redirect:toLogin";
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
	
	//显示最近的更新信息
	@RequestMapping("/showModify.do")
	@ResponseBody
	public TEmployee showModify(@RequestParam("id") Integer id) {
		TEmployee employee = employeeService.findById(id);
		System.out.println("根据"+id+ "得到的图书信息：" +employee);
		return employee;
	}
	
	//修改用户信息
	//在响应数据回浏览器时，指定json类型
	@RequestMapping(value = "/modifyUser.do")
	public String modifyUser(@RequestParam("avatar_file") MultipartFile avatar_file,TEmployee emp,HttpSession session,Model model) {
		log.debug("=======================");
		log.debug("=====avatar: " + avatar_file);
		log.debug("------实体数据before：" + emp);
		log.debug("------实体数据before版本号：" + emp.getVersion());
		if(!avatar_file.isEmpty()) {
			//上传文件
			String fileName;
			try {
				fileName = uploadFiles(avatar_file);
				//设置图片全名
				emp.setAvatarUrl(fileName);
			} catch (Exception e) {
				System.out.println("-----------图片信息有误！-----------");
				e.printStackTrace();
			}
		}
		//保存原值
		TEmployee oldEmp = employeeService.findByUser(global_username);
		if(oldEmp != null) {
			emp.setId(oldEmp.getId());
			emp.setRoleName(oldEmp.getRoleType() == 1 ? "超级管理员":"普通管理员");
			emp.setCreateTime(oldEmp.getCreateTime());
			emp.setLastModifyTime(LocalDateTime.now());
			emp.setVersion(oldEmp.getVersion());
			//判断新输入的手机号是否已存在
			TEmployee checkPhone = employeeService.findByUser(emp.getPhone());
			if(checkPhone != null) {
				model.addAttribute("CHECK_PHONE_ERROR", true);
				log.debug("。。。手机号重复！。。。。。。");
				//返回原值
				return "redirect:x_profile.do";
			}	
		}
		TEmployee employee = employeeService.update(emp);
		if(employee != null)
			log.debug("!!------实体数据after：" + employee);
			log.debug("!!------实体数据after版本号：" + employee.getVersion());
			global_username = employee.getPhone();
		session.setAttribute("LOGIN_USER", employee);
		System.out.println("更新后：" +employee);
		return "redirect:x_profile.do";
	}
	
	//修改密码
	@RequestMapping("/modifyPwd.do")
	public String modifyPassward(String oldPwd,String newPwd,String pwd2,Model model) {
		model.addAttribute("CHECK_PWD_ERROR",-1);
		if(!newPwd.equals(pwd2)) {
			model.addAttribute("CHECK_PWD_ERROR",1);
			return "x_modify_password";
		}
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
	
	
	/* ====================以下是公共方法区==================== */
	
	//文件上传
	public String uploadFiles(MultipartFile uploadFile) throws Exception{
		//定义文件名
		String fileName = ""; 
		//1.获取原始文件名 
		String uploadFileName = uploadFile.getOriginalFilename(); 
		//3.把文件加上随机数，防止文件重复 
		String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase(); 
		//4.判断是否输入了文件名 
		fileName = uuid + "_"+ uploadFileName;
		System.out.println(fileName);
		//2.获取文件路径
		String basePath = ResourceUtils.getURL("classpath:").getPath();
		System.out.println("basePath: "+basePath);
		//4.判断路径是否存在
		File realPath = new File(basePath,RESOURCE_PATH);
		System.out.println("filePath: "+realPath);
		if(!realPath.exists()) {
			System.out.println("创建目录结构。。。");
			realPath.mkdirs(); 
		} 
		//5.使用MulitpartFile接口中方法，把上传的文件写到指定位置 
		uploadFile.transferTo(new File(realPath,fileName)); 
		System.out.println("图片名："+fileName);
		return fileName;
	}
	
}

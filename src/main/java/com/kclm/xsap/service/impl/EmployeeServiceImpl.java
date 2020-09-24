package com.kclm.xsap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.service.EmployeeService;

import cn.hutool.extra.mail.MailUtil;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService{
	
	@Autowired
	private TEmployeeMapper employeeMapper;
	
	@Override
	public TEmployee login(String userName, String pwd) {
		return employeeMapper.findByNameAndPwd(userName,pwd);
	}
	
	
	@Override
	public boolean register(TEmployee emp) {
		//判断用户名（手机号）是否已存在
		TEmployee userName = employeeMapper.findByUserName(emp.getPhone());
		if(userName != null) {
			//用户名已存在
			System.out.println("用户名已存在！");
			return false;
		}
		//新增用户信息
		employeeMapper.insert(emp);
		return true;
	}
	
	/* 待处理  - begin*/
	/* 忘记密码，发送短信验证码 */
	@Override
	public boolean resetPwdByPhone(String phone) {
		
		return false;
	}
	/* 待处理  - end*/
	
	
	@Override
	public boolean resetPwdByEmail(String email,String content,Boolean isHtml) {
		
		MailUtil.send(email, "测试", content, isHtml);
		return false;
	}


	@Override
	public boolean updatePassword(String username, String oldPwd, String newPwd) {
		TEmployee employee = employeeMapper.findByNameAndPwd(username, oldPwd);
		if(employee == null) {
			return false;
		}
		employee.setRolePassword(newPwd);
		return true;
	}
	
	@Override
	public TEmployee update(TEmployee emp) {
		employeeMapper.updateById(emp);
		//用来查询刚更新的数据
		emp = employeeMapper.selectById(emp.getId());
		return emp;
	}

}

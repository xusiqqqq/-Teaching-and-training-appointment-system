package com.kclm.xsap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.service.EmployeeService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService{
	
	@Autowired
	private TEmployeeMapper employeeMapper;
	
	//登录
	@Override
	public TEmployee login(String userName, String pwd) {
		return employeeMapper.findByNameAndPwd(userName,pwd);
	}
	
	//注册
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
	
	//通过邮箱重置密码
	@Override
	public boolean resetPwdByEmail(String email,String content,Boolean isHtml) {
		//设置发件人信息
		MailAccount account = new MailAccount();
		account.setAuth(true);
		account.setHost("smtp.qq.com");
		account.setFrom("2735904505@qq.com");
		account.setPass("vvfoyqnnsvxqdfeg");
		//发送给目标邮箱
		try {
			MailUtil.send(account,CollUtil.newArrayList(email), "测试", content, isHtml);
		} catch (Exception e) {
			e.printStackTrace();
		}			
		
		return true;
	}

	//修改密码
	@Override
	public boolean updatePassword(String username, String oldPwd, String newPwd) {
		TEmployee employee = employeeMapper.findByNameAndPwd(username, oldPwd);
		if(employee == null) {
			return false;
		}
		employee.setRolePassword(newPwd);
		update(employee);
		return true;
	}
	
	//重置密码
	@Override
	public boolean resetPassward(String username,String newPwd) {
		System.out.println("username:" + username);
		TEmployee employee = employeeMapper.findByUserName(username);
		System.out.println("employee:"+ employee);
		if(employee == null)
			return false;
		employee.setRolePassword(newPwd);
		update(employee);
		return true;
	}
	
	//更新当前用户信息
	@Override
	public TEmployee update(TEmployee emp) {
		employeeMapper.updateById(emp);
		//用来查询刚更新的数据
		emp = employeeMapper.selectById(emp.getId());
		return emp;
	}

	
	//通过电话或邮箱找到用户信息
	@Override
	public TEmployee findByUser(String username) {
		TEmployee employee = employeeMapper.selectOne(new QueryWrapper<TEmployee>().eq("phone", username)
				.or().eq("role_email", username));
		return employee;
	}

	
}

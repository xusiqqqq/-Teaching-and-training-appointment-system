package com.kclm.xsap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private TEmployeeMapper employeeMapper;
	
	@Override
	public TEmployee login(String userName, String pwd) {
		
		return employeeMapper.findByNameAndPwd(userName,pwd);
	}

	@Override
	public void register(TEmployee emp) {
		//判断用户名是否已存在
		TEmployee userName = employeeMapper.findByUserName(emp.getRoleName());
		if(userName != null) {
			//用户名已存在
			System.out.println("用户名已存在！");
			return;
		}
		//新增用户信息
		employeeMapper.save(emp);
	}

}

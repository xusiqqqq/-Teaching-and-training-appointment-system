package com.kclm.xsap.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TEmployee;

@Mapper
public interface TEmployeeMapper extends BaseMapper<TEmployee> {

	/**
	 * 根据用户名和密码查询员工信息
	 * @param roleName	管理员用户名
	 * @param rolePassword	管理员密码
	 * @return	TEmployee。员工信息
	 */
	TEmployee findByNameAndPwd(@Param("roleName")String roleName,@Param("rolePassword") String rolePassword);

	/**
	 * 根据用户名查询员工信息
	 * @param roleName	管理员用户名
	 * @return	TEmployee。员工信息
	 */
	TEmployee findByUserName(String roleName);

}
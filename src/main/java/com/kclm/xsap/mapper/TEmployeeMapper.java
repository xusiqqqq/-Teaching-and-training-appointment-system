package com.kclm.xsap.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TEmployee;

@Mapper
public interface TEmployeeMapper extends BaseMapper<TEmployee> {

	TEmployee findByNameAndPwd(@Param("roleName")String roleName,@Param("rolePassword") String rolePassword);

	TEmployee findByUserName(String roleName);

	void save(TEmployee emp);

}
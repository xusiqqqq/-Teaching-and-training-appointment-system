package com.kclm.xsap.dao;

import com.kclm.xsap.entity.EmployeeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工表
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@Mapper
public interface EmployeeDao extends BaseMapper<EmployeeEntity> {
	
}

package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.EmployeeEntity;
import com.kclm.xsap.utils.PageUtils;

import java.util.Map;

/**
 * 员工表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
public interface EmployeeService extends IService<EmployeeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    EmployeeEntity isExistEmp(String username, String password);
}


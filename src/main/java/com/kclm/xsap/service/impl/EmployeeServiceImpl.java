package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.EmployeeDao;
import com.kclm.xsap.entity.EmployeeEntity;
import com.kclm.xsap.service.EmployeeService;


@Slf4j
@Service("employeeService")
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, EmployeeEntity> implements EmployeeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EmployeeEntity> page = this.page(
                new Query<EmployeeEntity>().getPage(params),
                new QueryWrapper<EmployeeEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public EmployeeEntity isExistEmp(String username, String password) {
        EmployeeEntity selectOneForLogin = this.baseMapper.selectOne(new QueryWrapper<EmployeeEntity>().eq("name", username).eq("role_password", password));
        log.debug("selectOneForLogin{}",selectOneForLogin);
        return selectOneForLogin;
    }
}
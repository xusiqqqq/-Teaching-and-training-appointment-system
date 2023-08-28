package com.kclm.xsap.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.kclm.xsap.dao.CourseCardDao;
import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.service.CourseCardService;
import com.kclm.xsap.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service("courseCardService")
public class CourseCardServiceImpl extends MppServiceImpl<CourseCardDao,CourseCardEntity> implements CourseCardService {
    @Resource
    private EmployeeService employeeService;

    @Resource
    private CourseCardDao dao;
    @Override
    public String[] getCardsNameByCourseId(Long courseId) {
        return dao.getCardNameByCourseId(courseId);
    }
    @Override
    public List<Long> getCardsIdByCourseId(Long courseId) {
        return dao.getCardIdByCourseId(courseId);
    }
}

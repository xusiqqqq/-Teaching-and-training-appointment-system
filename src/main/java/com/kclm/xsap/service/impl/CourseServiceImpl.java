package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.CourseDao;
import com.kclm.xsap.entity.CourseEntity;
import com.kclm.xsap.service.CourseService;


@Service("courseService")
public class CourseServiceImpl extends ServiceImpl<CourseDao, CourseEntity> implements CourseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CourseEntity> page = this.page(
                new Query<CourseEntity>().getPage(params),
                new QueryWrapper<CourseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CourseEntity> getCourseList() {
        return baseMapper.selectCourseList();
    }

}
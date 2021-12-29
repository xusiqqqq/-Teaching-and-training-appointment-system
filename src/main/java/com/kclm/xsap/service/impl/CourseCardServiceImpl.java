package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.CourseCardDao;
import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.service.CourseCardService;


@Service("courseCardService")
public class CourseCardServiceImpl extends ServiceImpl<CourseCardDao, CourseCardEntity> implements CourseCardService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CourseCardEntity> page = this.page(
                new Query<CourseCardEntity>().getPage(params),
                new QueryWrapper<CourseCardEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean saveCourseCard(List<CourseCardEntity> courseCardEntityList) {
        for (CourseCardEntity courseCardEntity : courseCardEntityList) {
            int i = baseMapper.saveCourseCard(courseCardEntity.getCardId(), courseCardEntity.getCourseId());
            if (i == Integer.MIN_VALUE + 1001) {
                return false;
            }
        }
        return true;
    }

}
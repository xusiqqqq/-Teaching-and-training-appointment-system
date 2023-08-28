package com.kclm.xsap.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.kclm.xsap.entity.CourseCardEntity;

import java.util.List;


public interface CourseCardService extends IMppService<CourseCardEntity> {
    //通过课程id获取课程所支持的会员卡名称
    String[] getCardsNameByCourseId(Long courseId);

    List<Long> getCardsIdByCourseId(Long courseId);
}

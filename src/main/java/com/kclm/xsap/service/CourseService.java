package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.CourseEntity;


public interface CourseService extends IService<CourseEntity> {
    public boolean saveCourseCard(CourseEntity course, Long[] cardListStr);

    public CourseEntity getCourseByScheduleId(Long scheduleId);
}

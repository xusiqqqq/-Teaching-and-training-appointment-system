package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.CourseDao;
import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.entity.CourseEntity;
import com.kclm.xsap.service.CourseCardService;
import com.kclm.xsap.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("courseService")
public class CourseServiceImpl extends ServiceImpl<CourseDao,CourseEntity> implements CourseService {
    @Resource
    private CourseDao dao;
    @Resource
    private CourseCardService courseCardService;


    /**
     * 存储 一个课程限制使用的多个会员卡 信息
     * @param course
     * @param cardListStr
     * @return
     */
    public boolean saveCourseCard(CourseEntity course, Long[] cardListStr) {
        CourseCardEntity courseCard = new CourseCardEntity();
        courseCard.setCourseId(course.getId());
        boolean b=true;
        if(cardListStr!=null&&cardListStr.length!=0){
            for (Long cardId : cardListStr) {
                courseCard.setCardId(cardId);
                b =courseCardService.save(courseCard);
                if(!b) break;
            }
        }
        return b;
    }

    @Override
    public CourseEntity getCourseByScheduleId(Long scheduleId) {
        return dao.getCourseEntityByScheduleId(scheduleId);
    }
}

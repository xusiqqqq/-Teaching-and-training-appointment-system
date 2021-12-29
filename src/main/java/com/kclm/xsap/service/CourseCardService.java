package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 中间表：课程-会员卡
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
public interface CourseCardService extends IService<CourseCardEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean saveCourseCard(List<CourseCardEntity> courseCardEntityList);
}


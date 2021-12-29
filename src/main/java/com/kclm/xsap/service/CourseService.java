package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.CourseEntity;
import com.kclm.xsap.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 课程表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
public interface CourseService extends IService<CourseEntity> {

    /**
     * 分页
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取课程信息列表
     * @return 课程信息
     */
    List<CourseEntity> getCourseList();
}


package com.kclm.xsap.dao;

import com.kclm.xsap.entity.CourseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 课程表
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@Mapper
public interface CourseDao extends BaseMapper<CourseEntity> {

    /**
     * 获取课程信息
     * @return 返回课程信息列表
     */
    List<CourseEntity> selectCourseList();
}

package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TScheduleRecord extends BaseEntity {

    /**
    * 课程号
    */
    private Long courseId;

    /**
    * 教师号
    */
    private Long teacherId;

    /**
    * 上课日期
    */
    private Date startDate;

    /**
    * 上课时间
    */
    private Date classTime;

    /**
    * 限制性别
    */
    private String limitSex;

    /**
    * 限制年龄
    */
    private Integer limitAge;

}
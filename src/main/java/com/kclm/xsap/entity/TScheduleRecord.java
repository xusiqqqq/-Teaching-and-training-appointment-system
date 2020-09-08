package com.kclm.xsap.entity;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TScheduleRecord extends BaseEntity {

    /**
    * 关联的课程
    */
    private List<TCourse> courses;

    /**
    * 关联的教师
    */
    private List<TEmployee> employees;

    /**
    * 上课日期
    */
    private LocalDate startDate;

    /**
    * 上课时间
    */
    private LocalTime classTime;

    /**
    * 限制性别
    */
    private String limitSex;

    /**
    * 限制年龄
    */
    private Integer limitAge;

}
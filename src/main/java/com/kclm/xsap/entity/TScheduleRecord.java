package com.kclm.xsap.entity;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_schedule_record",resultMap = "TScheduleRecordMap")
public class TScheduleRecord extends BaseEntity implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
    * 关联的课程
    */
	private Integer courseId;
	
	/**
	 *  封装课程实体数据
	 */
	@TableField(exist = false)
    private TCourse course;

    /**
    * 关联的教师
    */
    private Integer teacherId;
    
    /**
     *  封装教师实体数据
     */
    @TableField(exist = false)
    private TEmployee employee;

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
    * 限制年龄，-1 表示不限制
    */
    private Integer limitAge;

}
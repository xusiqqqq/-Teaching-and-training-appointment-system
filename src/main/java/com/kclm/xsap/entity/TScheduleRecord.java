package com.kclm.xsap.entity;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_schedule_record")
public class TScheduleRecord extends BaseEntity implements Serializable{

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
    * 关联的课程
    */
	private Long courseId;

	/**
	 *  封装课程实体数据
	 */
	@TableField(exist = false)
    @ToString.Exclude
    private TCourse course;

    /**
    * 关联的教师
    */
    private Long teacherId;

    /**
     *  封装教师实体数据
     */
    @TableField(exist = false)
    @ToString.Exclude
    private TEmployee employee;

    /**
     * 预约人数
     */
     private Integer orderNums;

    /**
    * 上课日期
    */
     @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
    * 上课时间
    */
     @DateTimeFormat(pattern = "HH:mm")
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

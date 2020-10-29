package com.kclm.xsap.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 下午4:42:29 
 * @description 此类用来描述了团课排课计划
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CourseScheduleDTO {

	private Long scheduleId;

	private Long courseId;

	/**
	 * 课程名
	 */
	private String courseName;
	
	/**
	 * 	课程颜色
	 */
	private String color;
	
	/**
	 * 上课准确时间
	 */
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime startTime;
	 
	 /**
	  * 	上课日期（拼接用）
	  */
	 private LocalDate classDate;
	 /**
	  * 	上课时间（拼接用）
	  */
	 private LocalTime classTime;
	
	/**
	 * 下课时间
	 */
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime endTime;
	
	/**
	 * 时长
	 */
	private Integer duration;
	
	/**
	 * 限制性别 【同课程中的一样，以课程中的为准，此处仅做为冗余字段】
	 */
	private String limitSex;
	
	/**
	 * 限制年龄 【同课程中的一样，以课程中的为准，此处仅做为冗余字段】
	 */
	private Integer limitAge;
	
	/**
	 * 支持的会员卡，多张会员卡名字之间以 | 隔开
	 */
	private String supportCards;
	
	/**
	 * 上课老师
	 */
	private String teacherName;
	
	/**
	 * 课堂容纳人数
	 */
	private Integer classNumbers;
	
	/**
	 * 	已预约人数
	 */
	private Integer orderNums;
	
	/**
	 * 	当前的课程需消耗次数
	 */
	private Integer timesCost;
	
	/**
	 * 	已预约记录
	 */
	private  List<ReserveRecordDTO> reservedList;
	
	/**
	 * 预约记录
	 */
	private List<ReserveRecordDTO> reserveRecord;
	
	/**
	 * 上课数据
	 */
	private List<ClassRecordDTO> classRecord;
}

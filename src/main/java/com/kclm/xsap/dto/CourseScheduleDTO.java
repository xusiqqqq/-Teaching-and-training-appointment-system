package com.kclm.xsap.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.kclm.xsap.entity.TMemberCard;

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
public class CourseScheduleDTO extends BaseDTO{

	/**
	 * 课程名
	 */
	private String courseName;
	
	/**
	 * 上课时间
	 */
	private LocalDateTime startTime;
	
	/**
	 * 下课时间
	 */
	private LocalDateTime endTime;
	
	/**
	 * 时长
	 */
	private Integer duration;
	
	/**
	 * 限制性别
	 */
	private String limitSex;
	
	/**
	 * 限制年龄
	 */
	private Integer limitAge;
	
	/**
	 * 支持的会员卡
	 */
	private List<String> supportCard;
	
	/**
	 * 上课老师
	 */
	private String teacher;
	
	/**
	 * 上课人数
	 */
	private Integer classNumbers;
	
	/**
	 * 预约记录
	 */
	private List<ReserveDTO> reserveRecord;
}

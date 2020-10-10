	package com.kclm.xsap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kclm.xsap.entity.TMember;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

	/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 下午4:06:37 
 * @description 此类用来描述了上课的记录信息
 *
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class ClassRecordDTO{

	/****
	 * 添加各自对应的id
	 * add by yejf
	 */
	private Long classRecordId;

	private Long courseId;

	private Long scheduleId;

	private Long cardId;
	
	private Long memberId;
	
	/**
	 *  会员信息
	 */
	private TMember member;
	
	/**
	 * 课程名
	 */
	private String courseName;
	
	/**
	 * 上课时间
	 */
	private LocalDateTime classTime;
	
	/**
	 * 授课老师
	 */
	private String teacherName;
	
	/**
	 * 会员卡名
	 */
	private String cardName;
	
	/**
	 * 上课人数
	 */
	private Integer classNumbers;
	
	/**
	 * 消耗卡次/节
	 */
	private Integer timesCost;
	
	/**
	 * 	涉及金额
	 */
	private BigDecimal involveMoney;
	
	/**
	 * 上课备注
	 */
	private String classNote;
	
	/**
	 * 教师评语
	 */
	private String comment;
	
	/**
	 * 上课状态检定。1，已上课；0，未上课
	 */
	private Integer checkStatus;
	
}

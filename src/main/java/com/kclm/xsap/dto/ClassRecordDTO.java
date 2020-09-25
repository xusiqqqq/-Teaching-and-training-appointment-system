package com.kclm.xsap.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class ClassRecordDTO{

	/****
	 * 添加各自对应的id
	 * add by yejf
	 */
	private Long classRecordId;

	private Long courseId;

	private Long scheduleId;

	private Long cardId;
	/**
	 * 课程名
	 */
	private String courseName;
	
	/**
	 * 上课时间
	 */
	private LocalDateTime classTime;
	
	/**
	 * 会员卡名
	 */
	private String cardName;
	
	/**
	 * 预约人数
	 */
	private Integer reserveNumbers;
	
	/**
	 * 消耗卡次/节
	 */
	private Integer timesCost;
	
	/**
	 * 预约备注
	 */
	private String reserveNote;
	
	/**
	 * 教师评语
	 */
	private String comment;
	
}

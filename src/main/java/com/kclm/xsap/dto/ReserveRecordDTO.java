package com.kclm.xsap.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 下午4:28:07 
 * @description 此类用来描述了预约记录
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ReserveRecordDTO{

	private Long courseId;

	private Long reserveId;

	private Long memberId;

	private Long scheduleId;
	/**
	 * 课程名
	 */
	private String courseName;
	
	/**
	 * 上课时间
	 */
	private LocalDateTime classTime;
	
	/**
	 * 会员名
	 */
	private String memberName;
	
	/**
	 * 手机号
	 */
	private String phone;
	
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
	 * 操作时间
	 */
	private LocalDateTime operateTime;
	
	/**
	 * 操作人
	 */
	private String operator;
	
	/**
	 * 预约备注
	 */
	private String reserveNote;
	
	/**
	 * 预约状态
	 */
	private Integer reserveStatus;
	
	/**
	 * 教师评语
	 */
	private String comment;
	
}

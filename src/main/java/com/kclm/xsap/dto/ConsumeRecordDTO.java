package com.kclm.xsap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 下午4:21:42 
 * @description 此类用来描述了消费记录
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ConsumeRecordDTO extends BaseDTO{

	/**
	 * 会员卡名
	 */
	private String cardName;
	
	/**
	 * 操作时间
	 */
	private LocalDateTime operateTime;
	
	/**
	 * 操作类型
	 */
	private String operateType;
	
	/**
	 * 卡次变化
	 */
	private Integer cardCountChange;
	
	/**
	 * 有效天数变化
	 */
	private Integer cardDayChange;
	
	/**
	 * 花费的金额
	 */
	private BigDecimal moneyCost;
	
	/**
	 * 操作员
	 */
	private String operator;
	
	/**
	 * 备注
	 */
	private String note;
	
	/**
	 * 激活状态。1，已激活；0，未激活
	 */
	private Integer status;
	
}

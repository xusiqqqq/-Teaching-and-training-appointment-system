package com.kclm.xsap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月22日 上午4:33:37 
 * @description 此类用来描述了会员卡对应的操作信息
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class MemberLogDTO extends BaseDTO {

	/**
	 * 操作时间
	 */
	private LocalDateTime operateTime;
	
	/**
	 * 操作类型
	 */
	private String operateType;
	
	/**
	 * 剩余可用次数
	 */
	private Integer validTimes;
	
	/**
	 * 卡到期日
	 */
	private LocalDateTime endToDate; 
	
	/**
	 * 金额
	 */
	private BigDecimal involveMoney;
	
	/**
	 * 操作人
	 */
	private String operator;
	
	/**
	 * 会员卡备注信息
	 */
	private String cardNote;
	
	/**
	 * 会员卡激活状态
	 */
	private Integer status;
	
}

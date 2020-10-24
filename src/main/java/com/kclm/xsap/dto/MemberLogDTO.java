package com.kclm.xsap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class MemberLogDTO {

	private Long id;
	/**
	 * 操作时间
	 */
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime operateTime;
	
	/**
	 * 操作类型
	 */
	private String operateType;
	
	/**
	 * 剩余可用次数,
	 */
	private Integer validTimes;
	
	/**
	 * 卡到期日: 来自于 会员绑卡记录 实体， 它的值由 日期 createTime 加上 validDay【有效期，天】 得到
	 */
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime endToDate; 
	
	/**
	 * 涉及金额， 来自于会员绑卡记录 实体， 它的值由 实收金额[receivedMoney] 除以 可使用次数【validCount】 得到
	 */
	private BigDecimal involveMoney;
	
	/**
	 * 操作人： 也就是当前的登录用户
	 */
	private String operator;
	
	/**
	 * 会员卡备注信息， 来自于会员卡 实体类
	 */
	private String cardNote;
	
	/**
	 * 会员卡激活状态， 来自于会员绑定会员卡的记录
	 */
	private Integer status;
	
}

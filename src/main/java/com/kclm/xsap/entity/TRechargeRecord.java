package com.kclm.xsap.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_recharge_record",resultMap = "TRechargeRecordMap")
public class TRechargeRecord extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
    * 关联的会员
    */
	private Long memberId;
	/**
	 *  封装会员实体数据
	 */
	@TableField(exist = false)
	@ToString.Exclude
    private TMember member;
	
	/**
	 *  用来封装会员卡实体
	 */
	@TableField(exist = false)
	@ToString.Exclude
	private TMemberCard card;
	
	/**
	 * 关联的会员卡
	 */
	private Long cardId;
	
    /**
    * 充值可用次数
    */
    private Integer addCount;

    /**
    * 延长有效天数
    */
    private Integer addDay;

    /**
    * 实收金额
    */
    private BigDecimal receivedMoney;

    /**
     * 支付方式
     */
    private String payMode;
    
    /**
     * 操作员
     */
    private String operator;
    
    private String note;

}
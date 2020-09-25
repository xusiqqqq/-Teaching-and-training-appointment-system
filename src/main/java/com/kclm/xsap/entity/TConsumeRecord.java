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
@TableName(value = "t_consume_record",resultMap = "TConsumeRecordMap")
public class TConsumeRecord extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 *	用来封装会员实体
	 */
	@TableField(exist = false)
    private TMember member;
	
	/**
	 * 	关联的会员
    */
	private Long memberId;
	
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
     *  花费的金额
     */
    private BigDecimal moneyCost;

    /**
    * 操作员
    */
    private String operator;

    private String note;

}
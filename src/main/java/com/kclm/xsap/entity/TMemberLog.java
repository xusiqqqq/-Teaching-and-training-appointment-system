package com.kclm.xsap.entity;


import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_member_log",resultMap = "TMemberLogMap")
public class TMemberLog extends BaseEntity implements Serializable{

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
    private TMember member;
	
	/**
	 *  用来封装会员卡实体
	 */
	@TableField(exist = false)
	private TMemberCard card;
	
	/**
	 * 关联的会员卡
	 */
	private Long cardId;
	
    /**
    * 操作类型
    */
    private String type;

    /**
     * 影响的金额
     */
    private BigDecimal involveMoney;
    
    /**
    * 操作员名称
    */
    private String operator;

}
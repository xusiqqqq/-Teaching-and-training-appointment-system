package com.kclm.xsap.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_member_bind_record",resultMap = "TMemberBindRecordMap")
public class TMemberBindRecord extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 关联的会员
	 */
	private Long memberId;
	/**
	 *  用来封装会员的实体数据
	 */
	@TableField(exist = false)
    private TMember member;
	
    /**
     * 关联的会员卡
     */
	private Long cardId;
	/**
	 *  用来封装会员卡的实体数据
	 */
	@TableField(exist = false)
    private TMemberCard card;

	
    /**
    * 可使用次数
    */
    private Integer validCount;

    /**
    * 充值有效期，按天算
    */
    private Integer validDay;

    /**
    * 实收金额
    */
    private BigDecimal receivedMoney;

    private String note;

}
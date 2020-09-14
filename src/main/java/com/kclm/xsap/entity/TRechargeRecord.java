package com.kclm.xsap.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
	private Integer memberId;
	/**
	 *  封装会员实体数据
	 */
	@TableField(exist = false)
    private TMember member;
	
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

    private String note;

}